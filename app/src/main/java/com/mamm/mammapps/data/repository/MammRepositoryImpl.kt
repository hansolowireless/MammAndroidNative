package com.mamm.mammapps.data.repository

import androidx.core.net.toUri
import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.Genre
import com.mamm.mammapps.data.model.GetBrandedContentResponse
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.data.model.Subgenre
import com.mamm.mammapps.data.model.exception.GetMemoriesException
import com.mamm.mammapps.data.model.memories.GetMemoriesResponse
import com.mamm.mammapps.data.model.serie.GetSeasonInfoResponse
import com.mamm.mammapps.data.datasource.session.SessionDatasource
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.util.AppConstants
import java.time.Duration
import java.time.ZonedDateTime
import javax.inject.Inject

class MammRepositoryImpl @Inject constructor(
    private val remoteDatasource: RemoteDatasource,
    private val localDataSource: LocalDataSource,
    private val sessionManager: SessionDatasource,
    private val logger: Logger
) : MammRepository {

    companion object {
        private const val TAG = "MammRepositoryImpl"
    }

    override suspend fun getHomeContent(): Result<GetHomeContentResponse> {
        return runCatching {
            localDataSource.getHomeContent()?.let { return@runCatching it }
            val response = remoteDatasource.getHomeContent()
            localDataSource.setHomeContent(response)
            localDataSource.setCachedSubgenreList(
                response.genres?.flatMap { genre ->
                    genre.subgenres ?: emptyList()
                } ?: emptyList()
            )
            response
        }.onSuccess { response ->
            logger.debug(TAG, "getHomeContent Received and saved successful response")
        }.onFailure {
            logger.error(TAG, "getHomeContent Failed: ${it}")
        }
    }

    override suspend fun getMovies(): Result<GetOtherContentResponse> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))

        return runCatching {
            localDataSource.getMoviesContent()?.let { return@runCatching it }
            val response = remoteDatasource.getMovies(jsonParam)
            localDataSource.setMoviesContent(response)
            response
        }.onSuccess { response ->
            logger.debug(TAG, "getMovies Received and saved successful response")
        }
    }

    override suspend fun getDocumentaries(): Result<GetOtherContentResponse> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))

        return runCatching {
            localDataSource.getDocumentariesContent()?.let { return@runCatching it }
            val response = remoteDatasource.getDocumentaries(jsonParam)
            localDataSource.setDocumentariesContent(response)
            response
        }.onSuccess { response ->
            logger.debug(TAG, "getDocumentaries Received and saved successful response")
        }
    }

    override suspend fun getAdults(): Result<GetBrandedContentResponse> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))

        return runCatching {
            localDataSource.getAdultsContent()?.let { return@runCatching it }
            val response = remoteDatasource.getAdults(jsonParam)
            localDataSource.setAdultsContent(response)
            response
        }.onSuccess { response ->
            logger.debug(TAG, "getAdults Received and saved successful response")
        }
    }

    override suspend fun getKids(): Result<GetOtherContentResponse> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))

        return runCatching {
            localDataSource.getKidsContent()?.let { return@runCatching it }
            val response = remoteDatasource.getKids(jsonParam)
            localDataSource.setKidsContent(response)
            response
        }.onSuccess { response ->
            logger.debug(TAG, "getKids Received and saved successful response")
        }
    }

    override suspend fun getSports(): Result<GetOtherContentResponse> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))

        return runCatching {
            localDataSource.getSportsContent()?.let { return@runCatching it }
            val response = remoteDatasource.getSports(jsonParam)
            localDataSource.setSportsContent(response)
            response
        }.onSuccess { response ->
            logger.debug(TAG, "getSports Received and saved successful response")
        }
    }

    override suspend fun getWarner(): Result<GetBrandedContentResponse> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result
                .failure(IllegalStateException("No valid path segment found in session file"))
        return runCatching {
            localDataSource.getWarnerContent()?.let { return@runCatching it }
            val response = remoteDatasource.getWarner(jsonParam)
            localDataSource.setWarnerContent(response)
            response
        }.onSuccess { response ->
            logger.debug(TAG, "getWarner Received and saved successful response")
        }
    }

    override suspend fun getAcontra(): Result<GetBrandedContentResponse> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result
                .failure(IllegalStateException("No valid path segment found in session file"))
        return runCatching {
            localDataSource.getAcontraContent()?.let { return@runCatching it }
            val response = remoteDatasource.getAcontra(jsonParam)
            localDataSource.setAcontraContent(response)
            response
        }.onSuccess { response ->
            logger.debug(TAG, "getAcontra Received and saved successful response")
        }
    }

    override suspend fun getAMC(): Result<GetBrandedContentResponse> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result
                .failure(IllegalStateException("No valid path segment found in session file"))
        return runCatching {
            localDataSource.getAMCContent()?.let { return@runCatching it }
            val response = remoteDatasource.getAMC(jsonParam)
            localDataSource.setAMCContent(response)
            response
        }.onSuccess { response ->
            logger.debug(TAG, "getAMC Received and saved successful response")
        }
    }

    override suspend fun getMemories(): Result<GetMemoriesResponse> {
        return runCatching {
            localDataSource.getMyMemories()?.let { return@runCatching it }
            val response = remoteDatasource.getMyMemories()
            if (response.sections.isNullOrEmpty()) {
                throw GetMemoriesException.EmptyList
            }
            localDataSource.setMyMemories(response)
            response
        }.onFailure {
            // Este bloque ahora capturará tanto errores de red como tu excepción personalizada
            logger.error(TAG, "getMemories failed: ${it.message}, $it")
        }
    }

    override suspend fun getSeasonsInfo(serieId: Int): Result<GetSeasonInfoResponse> {
        return runCatching {
            remoteDatasource.getSeasonInfo(serieId)
        }.onSuccess {
            logger.debug(TAG, "getSeasonsInfo Received successful response")
        }
    }

    override suspend fun getExpandedCategoryContent(categoryId: Int): Result<GetBrandedContentResponse> {
        return runCatching {
            remoteDatasource.getExpandedCategory(categoryId)
        }.onSuccess {
            logger.debug(TAG, "getExpandedCategory Received successful response")
        }
    }

    override fun findHomeContent(identifier: ContentIdentifier): Result<Any>? {
        var content: Any? = when (identifier) {
            is ContentIdentifier.Channel -> localDataSource.getHomeContent()?.channels?.find { it.id == identifier.id }
            is ContentIdentifier.VoD -> localDataSource.getHomeContent()?.contents?.find { it.id == identifier.id }
            is ContentIdentifier.Event -> localDataSource.getHomeContent()?.events?.find { it.id == identifier.id }
            is ContentIdentifier.Serie -> localDataSource.getHomeContent()?.series?.find { it.id == identifier.id }
        }

        if (content == null) {
            //If content is STILL null, try to find it in the featured list
            content =
                localDataSource.getHomeContent()?.featured?.find { it.id == identifier.id }
        }

        return content?.let { Result.success(it) }
    }

    override fun findMovieContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> localDataSource.getMoviesContent()?.vods?.find { it.getId() == identifier.id }
            is ContentIdentifier.Event -> localDataSource.getMoviesContent()?.events?.find { it.getId() == identifier.id }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findDocumentaryContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> localDataSource.getDocumentariesContent()?.vods?.find { it.getId() == identifier.id }
            is ContentIdentifier.Event -> localDataSource.getDocumentariesContent()?.events?.find { it.getId() == identifier.id }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findAdultContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> localDataSource.getAdultsContent()?.vods?.find { it.getId() == identifier.id }
            is ContentIdentifier.Event -> localDataSource.getAdultsContent()?.events?.find { it.getId() == identifier.id }
            is ContentIdentifier.Channel -> localDataSource.getHomeContent()?.channels?.find { it.id == identifier.id }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findSportsContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> localDataSource.getSportsContent()?.vods?.find { it.getId() == identifier.id }
            is ContentIdentifier.Event -> localDataSource.getSportsContent()?.events?.find { it.getId() == identifier.id }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findKidsContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> localDataSource.getKidsContent()?.vods?.find { it.getId() == identifier.id }
            is ContentIdentifier.Event -> localDataSource.getKidsContent()?.events?.find { it.getId() == identifier.id }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findWarnerContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> {
                localDataSource.getWarnerContent()?.vods?.find { it.getId() == identifier.id }
                    ?: localDataSource.getWarnerContent()?.featured?.find { it.formatId == identifier.id.toString() }
            }

            else -> null
        }
        return content?.let { Result.success(it) }
    }

    override fun findAcontraContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> {
                localDataSource.getAcontraContent()?.vods?.find { it.getId() == identifier.id }
                    ?: localDataSource.getAcontraContent()?.featured?.find { it.formatId == identifier.id.toString() }
            }

            else -> null
        }
        return content?.let { Result.success(it) }
    }

    override fun findAMCContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> {
                localDataSource.getAMCContent()?.vods?.find { it.getId() == identifier.id }
                    ?: localDataSource.getAMCContent()?.featured?.find { it.formatId == identifier.id.toString() }
            }

            else -> null
        }
        return content?.let { Result.success(it) }
    }

    override fun findMemoriesContent(identifier: ContentIdentifier): Result<Any>? {
        val memories = localDataSource.getMyMemories() ?: return null

        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> {
                memories.sections
                    ?.flatMap { it.items }
                    ?.find { it.id == identifier.id }
            }

            else -> null
        }

        return content?.let {
            Result.success(it)
        }
    }

    override fun findGenreWithId(id: Int): Result<Genre> {
        return runCatching {
            localDataSource.getHomeContent()
                ?.genres
                ?.firstOrNull { it.id == id }
                ?: throw NoSuchElementException("Genre with id $id not found")
        }
    }

    override fun findChannelWithId(id: Int): Result<Channel> {
        return runCatching {
            localDataSource.getHomeContent()
                ?.channels
                ?.find { it.id == id }
                ?: throw NoSuchElementException("Channel with id $id not found")
        }
    }

    override fun shouldRequestPin(): Boolean {

        if (sessionManager.pinParental.isNullOrBlank()) {
            logger.info(TAG, "No PIN found for User, No PIN request is needed.")
            return false
        }

        // Obtener la última fecha guardada desde el LocalDataSource.
        val lastPinTime: ZonedDateTime? = localDataSource.getLastTimePinWasCorrect()

        // Si nunca se ha guardado una fecha (es nulo), debemos solicitar el PIN.
        if (lastPinTime == null) {
            logger.debug(TAG, "No ZonedDateTime found, PIN request is needed.")
            return true
        }

        // Obtener la hora actual con la misma zona horaria.
        val currentTime = ZonedDateTime.now(lastPinTime.zone)

        // Calcular la duración entre la fecha guardada y la actual.
        val duration = Duration.between(lastPinTime, currentTime)

        // Comparar la duración con 15 minutos.
        val shouldRequest = duration.toMinutes() > AppConstants.PIN_REQUEST_MINS

        if (shouldRequest) {
            logger.debug(TAG, "More than 15 minutes have passed, PIN request is needed.")
        } else {
            logger.debug(TAG, "Less than 15 minutes have passed, no PIN request needed.")
        }

        return shouldRequest
    }


    override fun validatePin(pin: String): Boolean {
        // La lógica de validación real.
        // Asumo que el PIN correcto está en el SessionManager.
        val correctPin = sessionManager.pinParental
        val isCorrect = pin == correctPin

        if (isCorrect) {
            logger.debug(TAG, "PIN validation successful.")
        } else {
            logger.error(TAG, "PIN validation failed.")
        }

        return isCorrect
    }

    override fun savePinSuccessTimestamp() {
        // Guarda la marca de tiempo actual en el LocalDataSource.
        val currentTime = ZonedDateTime.now()
        localDataSource.setLastTimePinWasCorrect(currentTime)
        logger.debug(TAG, "Saved new PIN success timestamp: $currentTime")
    }

    override fun getSubgenreList(): Result<List<Subgenre>> {
        return runCatching {
            localDataSource.getCachedSubgenreList()
                ?: throw NoSuchElementException("Subgenre list not found in cache or is null")
        }
    }


}