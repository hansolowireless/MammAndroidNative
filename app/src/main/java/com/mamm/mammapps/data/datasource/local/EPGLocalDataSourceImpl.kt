package com.mamm.mammapps.data.datasource.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.epg.EPGChannelContentDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EPGLocalDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
    private val logger: Logger
) : EPGLocalDataSource {

    companion object {
        private const val TAG = "EPGLocalDataSource"
        private const val DIR_NAME = "epg"
        private const val OWNER_FILE = "owner"
        private const val PREFIX = "epg-"
        private const val SUFFIX = ".json"
        private const val TMP_SUFFIX = ".tmp"
    }

    // Serializa el acceso al directorio: la parrilla de varios días puede cargarse a la vez
    private val mutex = Mutex()

    private val listType = object : TypeToken<List<EPGChannelContentDto>>() {}.type

    private fun directory(): File =
        File(context.cacheDir, DIR_NAME).apply { if (!exists()) mkdirs() }

    private fun fileFor(date: LocalDate) = File(directory(), "$PREFIX$date$SUFFIX")

    override suspend fun getEPG(date: LocalDate): List<EPGChannelContentDto>? =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val file = fileFor(date)
                if (!file.exists()) return@withLock null

                runCatching {
                    gson.fromJson<List<EPGChannelContentDto>>(file.readText(), listType)
                }.getOrElse { error ->
                    // Fichero corrupto o de un formato anterior: se descarta y se vuelve a pedir
                    logger.error(TAG, "getEPG - parrilla de $date ilegible, se descarta: ${error.message}")
                    file.delete()
                    null
                }?.takeIf { it.isNotEmpty() }
            }
        }

    override suspend fun setEPG(date: LocalDate, channels: List<EPGChannelContentDto>) {
        if (channels.isEmpty()) return
        withContext(Dispatchers.IO) {
            mutex.withLock {
                runCatching {
                    // Se escribe a un temporal y se renombra: un corte a media escritura
                    // dejaría el .tmp a medias, nunca un JSON roto en el fichero bueno
                    val target = fileFor(date)
                    val tmp = File(directory(), "${target.name}$TMP_SUFFIX")
                    tmp.writeText(gson.toJson(channels, listType))
                    if (!tmp.renameTo(target)) {
                        tmp.copyTo(target, overwrite = true)
                        tmp.delete()
                    }
                }.onFailure { error ->
                    logger.error(TAG, "setEPG - no se pudo guardar la parrilla de $date: ${error.message}")
                }
            }
        }
    }

    override suspend fun purgeOlderThan(oldest: LocalDate) {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                runCatching {
                    directory().listFiles()?.forEach { file ->
                        val name = file.name
                        when {
                            name.endsWith(TMP_SUFFIX) -> file.delete() // restos de escrituras cortadas
                            name.startsWith(PREFIX) && name.endsWith(SUFFIX) -> {
                                val date = runCatching {
                                    LocalDate.parse(name.removePrefix(PREFIX).removeSuffix(SUFFIX))
                                }.getOrNull()
                                if (date != null && date.isBefore(oldest)) {
                                    file.delete()
                                    logger.debug(TAG, "purgeOlderThan - borrada la parrilla de $date")
                                }
                            }
                        }
                    }
                }.onFailure { error ->
                    logger.error(TAG, "purgeOlderThan - fallo limpiando la caché: ${error.message}")
                }
            }
        }
    }

    override suspend fun clearIfUserChanged(userKey: String?) {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                runCatching {
                    val ownerFile = File(directory(), OWNER_FILE)
                    val stored = if (ownerFile.exists()) ownerFile.readText() else null
                    val incoming = userKey?.let { hash(it) }

                    if (stored == incoming) return@runCatching

                    logger.debug(TAG, "clearIfUserChanged - usuario distinto, se vacía la caché")
                    directory().listFiles()?.forEach { it.delete() }
                    if (incoming != null) File(directory(), OWNER_FILE).writeText(incoming)
                }.onFailure { error ->
                    logger.error(TAG, "clearIfUserChanged - fallo comprobando el propietario: ${error.message}")
                }
            }
        }
    }

    /** El usuario no se guarda en claro: solo hace falta poder comparar. */
    private fun hash(value: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
