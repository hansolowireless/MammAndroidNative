package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.local.EPGLocalDataSource
import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.datasource.session.SessionDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.mapper.toDomain
import com.mamm.mammapps.data.model.epg.EPGChannelContentDto
import com.mamm.mammapps.domain.interfaces.EPGRepository
import com.mamm.mammapps.domain.model.epg.EPGChannelContent
import com.mamm.mammapps.domain.model.epg.MultiDayEPG
import com.mamm.mammapps.domain.model.entity.Event
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class EPGRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDatasource,
    private val localDataSource: LocalDataSource,
    private val epgLocalDataSource: EPGLocalDataSource,
    private val sessionDatasource: SessionDatasource,
    private val logger: Logger
) : EPGRepository {

    companion object {
        private const val TAG = "EPGRepositoryImpl"

        // El selector de fechas llega hasta 6 días atrás, y ese día necesita además el
        // fichero de la víspera para completarse. De ahí el séptimo.
        private const val RETENTION_DAYS = 7L

        // Y hasta 2 días adelante, que por lo mismo necesitan un tercero.
        private const val SELECTOR_DAYS_AHEAD = 2L

        // Tras un fallo cargando el fichero del directo, tiempo antes de volver a intentarlo
        private const val LIVE_RETRY_MS = 60_000L
    }

    // Volatile porque el reproductor lo lee desde otro hilo mientras se puebla
    @Volatile
    private var cachedMultiDayEPG: MultiDayEPG? = null

    // Último intento fallido de cargar el fichero del directo, para no reintentarlo en bucle
    @Volatile
    private var lastLiveLoadFailure: Pair<LocalDate, Long>? = null

    // Dos fechas pueden cargarse a la vez (cerrojo por día), y añadir una entrada es un
    // lee-modifica-escribe sobre el mapa: sin esto una de las dos se perdería.
    private val memoryLock = Any()

    // Un cerrojo por día: si dos pantallas piden la misma fecha a la vez, la segunda
    // espera y la encuentra ya en memoria en vez de repetir las peticiones por canal.
    // Es por día y no global para que precargar mañana no bloquee la parrilla que el
    // usuario está mirando.
    private val dayLocks = HashMap<LocalDate, Mutex>()

    private fun lockFor(date: LocalDate): Mutex = synchronized(dayLocks) {
        dayLocks.getOrPut(date) { Mutex() }
    }

    override suspend fun getEPG(date: LocalDate): Result<List<EPGChannelContent>> {
        return runCatching {
            val zone = ZoneId.systemDefault()

            // Cada fichero se carga con su propio cerrojo, cogido y suelto de uno en uno:
            // así dos pantallas no descargan el mismo día a la vez y no hay interbloqueo.
            val loaded = sourceFilesFor(date, zone).associateWith { source ->
                lockFor(source).withLock { loadDay(source) }
            }

            assembleLocalDay(date, zone, loaded)
        }
    }

    /**
     * Ficheros del servidor necesarios para armar el día local [date].
     *
     * El servidor corta los ficheros por la medianoche UTC, así que un día local solo cabe
     * entero en un fichero si el dispositivo está en UTC. En cualquier otra zona el día
     * local sobresale por un extremo y hace falta el fichero vecino de ese lado:
     *
     *  - Desfase positivo (España, UTC+1/+2): el día local empieza antes de donde arranca
     *    su fichero, así que la primera hora o dos están en el fichero anterior.
     *  - Desfase negativo (Colombia, UTC-5): el día local acaba después de donde termina su
     *    fichero, y son las últimas cinco horas las que están en el siguiente.
     */
    private fun sourceFilesFor(date: LocalDate, zone: ZoneId): List<LocalDate> {
        val offset = zone.rules.getOffset(date.atStartOfDay(zone).toInstant()).totalSeconds
        return when {
            offset > 0 -> listOf(date.minusDays(1), date)
            offset < 0 -> listOf(date, date.plusDays(1))
            else -> listOf(date)
        }
    }

    /**
     * Memoria, disco y por último red. En disco se guarda el fichero del servidor tal cual,
     * nunca el día local ya armado: el reparto entre ficheros depende de la zona horaria
     * del dispositivo y cambia con el horario de verano.
     */
    private suspend fun loadDay(date: LocalDate): List<EPGChannelContent> {
        cachedMultiDayEPG?.multiDayEPG?.get(date)?.let { return it }

        epgLocalDataSource.getEPG(date)?.let { cached ->
            logger.debug(TAG, "loadDay - fichero de $date recuperado de disco")
            return cached.toDomainContent().also { putInMemory(date, it) }
        }

        return fetchFromApi(date)
    }

    private suspend fun fetchFromApi(date: LocalDate): List<EPGChannelContent> {
        val homeContentDto = localDataSource.getHomeContent()
        val failedChannels = AtomicInteger(0)

        val channelContents = coroutineScope {
            homeContentDto?.channels
                ?.mapNotNull { channelDto -> channelDto.id?.let { channelDto to it } }
                ?.map { (channelDto, channelId) ->
                    async {
                        runCatching {
                            val epgResponseDto = remoteDataSource.getChannelEPG(channelId, date)
                            if (epgResponseDto.events?.isNotEmpty() == true) {
                                EPGChannelContentDto(channel = channelDto, events = epgResponseDto.events)
                            } else null
                        }.onFailure { error ->
                            failedChannels.incrementAndGet()
                            logger.error(TAG, "Error obteniendo EPG para canal $channelId: ${error.message}")
                        }.getOrNull()
                    }
                }
                ?.awaitAll()
                ?.filterNotNull()
                ?: emptyList()
        }

        // Solo se persiste un día completo. Como la caché no caduca, guardar una parrilla
        // a la que le faltan canales por un fallo de red la dejaría coja para siempre.
        // La lista vacía se descarta por lo mismo: si aún no hay home content en memoria
        // no hay canales que recorrer, y ese día quedaría vacío para siempre.
        when {
            channelContents.isEmpty() ->
                logger.warn(TAG, "fetchFromApi - $date no se guarda en disco: sin canales")

            failedChannels.get() > 0 ->
                logger.warn(TAG, "fetchFromApi - $date no se guarda en disco: ${failedChannels.get()} canales fallaron")

            else -> {
                epgLocalDataSource.setEPG(date, channelContents)
                // La caché de disco solo crece al guardar un día nuevo: limpiar justo aquí la
                // mantiene acotada sin que nadie tenga que acordarse de llamar a nada.
                epgLocalDataSource.purgeOlderThan(currentWindow().start)
            }
        }

        val domain = channelContents.toDomainContent()
        // Vacía no se guarda ni en memoria: quedaría como "ya cargado" y el día saldría vacío
        // toda la sesión aunque luego sí hubiera canales.
        if (domain.isNotEmpty()) putInMemory(date, domain)
        return domain
    }

    private fun putInMemory(date: LocalDate, content: List<EPGChannelContent>) {
        synchronized(memoryLock) {
            val updated = cachedMultiDayEPG?.multiDayEPG?.toMutableMap() ?: mutableMapOf()
            updated[date] = content

            // Sin esto, una app que se queda abierta días va acumulando parrillas que ya no
            // se pueden mostrar: cada día son del orden de un par de megas. Se conserva la
            // ventana del selector más un día por cada lado, que es el fichero vecino que
            // necesitan los extremos para completarse.
            val window = currentWindow()
            cachedMultiDayEPG = MultiDayEPG(updated.filterKeys { it in window })

            synchronized(dayLocks) {
                // Un cerrojo en uso no se quita: si se recreara, dos corrutinas podrían
                // entrar a la vez a cargar el mismo día.
                val stale = dayLocks.filter { (date, lock) -> date !in window && !lock.isLocked }
                dayLocks.keys.removeAll(stale.keys)
            }
        }
    }

    private fun currentWindow(): ClosedRange<LocalDate> {
        val today = LocalDate.now()
        return today.minusDays(RETENTION_DAYS)..today.plusDays(SELECTOR_DAYS_AHEAD + 1)
    }

    private fun List<EPGChannelContentDto>.toDomainContent(): List<EPGChannelContent> =
        mapNotNull { runCatching { it.toDomain() }.getOrNull() }

    /**
     * Arma la parrilla del día local [date] a partir de los ficheros de [loaded], quedándose
     * con los eventos que se solapan con las 24 horas locales de ese día.
     *
     * El fichero del propio día manda en qué canales hay y en qué orden; los vecinos solo
     * aportan eventos. Se hace al leer y no se persiste, porque el reparto entre ficheros
     * depende de la zona del dispositivo.
     */
    private fun assembleLocalDay(
        date: LocalDate,
        zone: ZoneId,
        loaded: Map<LocalDate, List<EPGChannelContent>>
    ): List<EPGChannelContent> {
        val dayStart = date.atStartOfDay(zone)
        val dayEnd = date.plusDays(1).atStartOfDay(zone)

        val own = loaded[date].orEmpty()
        val neighbours = loaded.filterKeys { it != date }.values.flatten()

        val result = own.map { content ->
            val extra = neighbours
                .filter { it.channel.id == content.channel.id }
                .flatMap { it.events }

            val events = (content.events + extra)
                .distinctBy { it.getId() }
                .filter { it.overlaps(dayStart, dayEnd) }
                .sortedWith(compareBy(nullsLast<ZonedDateTime>()) { it.startDateTime })

            if (events == content.events) content else content.copy(events = events)
        }

        logger.debug(
            TAG,
            "assembleLocalDay - $date armado con ${loaded.keys.sorted()} " +
                "(${result.size} canales, ${result.sumOf { it.events.size }} eventos)"
        )
        return result
    }

    /** Cierto si el evento ocupa algo del intervalo, aunque empiece antes o acabe después. */
    private fun Event.overlaps(from: ZonedDateTime, to: ZonedDateTime): Boolean {
        val start = startDateTime ?: return false
        val end = endDateTime ?: return false
        return end.isAfter(from) && start.isBefore(to)
    }

    /**
     * Parrilla de un canal concreto en el día local [date], armada con lo que haya en memoria
     * y sin tocar red ni disco. Se arma solo ese canal y no el día entero.
     */
    private fun channelFromMemory(date: LocalDate, channelId: Int): EPGChannelContent? {
        val zone = ZoneId.systemDefault()
        val loaded = sourceFilesFor(date, zone).associateWith { source ->
            cachedMultiDayEPG?.multiDayEPG?.get(source)
                ?.filter { it.channel.id == channelId }
                .orEmpty()
        }
        if (loaded[date].isNullOrEmpty()) return null
        return assembleLocalDay(date, zone, loaded).firstOrNull()
    }

    override suspend fun getLiveEventForChannel(channelId: Int): Event? {
        // El programa en emisión está siempre en el fichero de la fecha UTC actual: cada
        // fichero empieza con el que cruza su medianoche UTC. Para esto no hace falta armar
        // el día local, que solo sirve para pintar la rejilla.
        val file = LocalDate.now(ZoneOffset.UTC)
        val channels = cachedMultiDayEPG?.multiDayEPG?.get(file) ?: loadForLiveLookup(file)

        return channels.firstOrNull { it.channel.id == channelId }
            ?.events?.find { it.isLive() }
    }

    /**
     * Carga el fichero que necesita la consulta del directo cuando no está en memoria, que es
     * lo que pasa al cambiar de día con la app abierta o en segundo plano.
     *
     * Si acaba de fallar no se reintenta enseguida: esta consulta la hacen el zapping canal por
     * canal y Home en cada cambio de foco, y sin red cada intento lanzaría una petición por canal.
     */
    private suspend fun loadForLiveLookup(file: LocalDate): List<EPGChannelContent> {
        lastLiveLoadFailure?.let { (date, at) ->
            if (date == file && System.currentTimeMillis() - at < LIVE_RETRY_MS) return emptyList()
        }

        val loaded = lockFor(file).withLock { loadDay(file) }
        lastLiveLoadFailure = if (loaded.isEmpty()) file to System.currentTimeMillis() else null
        return loaded
    }

    override fun findContent(channelId: Int, eventId: Int, date: LocalDate): Event {
        return channelFromMemory(date, channelId)
            ?.events?.find { it.getId() == eventId }
            ?: throw IllegalStateException("Content not found")
    }

    override fun clearCache() {
        synchronized(memoryLock) { cachedMultiDayEPG = null }
    }

    override suspend fun onUserLoggedIn() {
        clearCache()
        epgLocalDataSource.clearIfUserChanged(sessionDatasource.getUserCredentials().first)
    }
}
