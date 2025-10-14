package com.mamm.mammapps.data.repository

import androidx.core.net.toUri
import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.extension.transformData
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.Genre
import com.mamm.mammapps.data.model.GetBrandedContentResponse
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.data.model.serie.GetSeasonInfoResponse
import com.mamm.mammapps.data.session.SessionManager
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.util.AppConstants
import java.time.Duration
import java.time.ZonedDateTime
import javax.inject.Inject

class MammRepositoryImpl @Inject constructor(
    private val remoteDatasource: RemoteDatasource,
    private val localDataSource: LocalDataSource,
    private val sessionManager: SessionManager,
    private val logger: Logger
) : MammRepository {

    companion object {
        private const val TAG = "MammRepositoryImpl"
    }

    override suspend fun getHomeContent(): Result<GetHomeContentResponse> {
        return runCatching {
            remoteDatasource.getHomeContent()
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
            remoteDatasource.getMovies(jsonParam)
        }.onSuccess { response ->
            logger.debug(TAG, "getMovies Received and saved successful response")
        }
    }

    override suspend fun getDocumentaries(): Result<GetOtherContentResponse> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))

        return runCatching {
            remoteDatasource.getDocumentaries(jsonParam)
        }.onSuccess { response ->
            logger.debug(TAG, "getMovies Received and saved successful response")
        }
    }

    override suspend fun getAdults(): Result<GetBrandedContentResponse> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))

        return runCatching {
            remoteDatasource.getAdults(jsonParam)
        }.onSuccess { response ->
            logger.debug(TAG, "getAdults Received and saved successful response")
        }
    }

    override suspend fun getKids(): Result<GetOtherContentResponse> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))

        return runCatching {
            remoteDatasource.getKids(jsonParam)
        }.onSuccess { response ->
            logger.debug(TAG, "getKids Received and saved successful response")
        }
    }

    override suspend fun getSports(): Result<GetOtherContentResponse> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))

        return runCatching {
            remoteDatasource.getSports(jsonParam)
        }.onSuccess { response ->
            logger.debug(TAG, "getSports Received and saved successful response")
        }
    }

    override suspend fun getWarner(): Result<GetBrandedContentResponse> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result
                .failure(IllegalStateException("No valid path segment found in session file"))
        return runCatching {
            remoteDatasource.getWarner(jsonParam)
        }.onSuccess { response ->
            logger.debug(TAG, "getWarner Received and saved successful response")
        }
    }

    override suspend fun getAcontra(): Result<GetBrandedContentResponse> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result
                .failure(IllegalStateException("No valid path segment found in session file"))
        return runCatching {
            remoteDatasource.getAcontra(jsonParam)
        }.onSuccess { response ->
            logger.debug(TAG, "getAcontra Received and saved successful response")
        }
    }

    override suspend fun getAMC(): Result<GetBrandedContentResponse> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result
                .failure(IllegalStateException("No valid path segment found in session file"))
        return runCatching {
            remoteDatasource.getAMC(jsonParam)
        }.onSuccess { response ->
            logger.debug(TAG, "getAMC Received and saved successful response")
        }
    }

    override suspend fun getSeasonsInfo(serieId: Int): Result<GetSeasonInfoResponse> {
        return runCatching {
            remoteDatasource.getSeasonInfo(serieId)
        }.onSuccess { response ->
            logger.debug(TAG, "getSeasonsInfo Received successful response")
        }
    }

    override fun findHomeContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.Channel -> remoteDatasource.getCachedHomeContent()?.channels?.find { it.id == identifier.id }
            is ContentIdentifier.VoD -> remoteDatasource.getCachedHomeContent()?.contents?.find { it.id == identifier.id }
            is ContentIdentifier.Event -> remoteDatasource.getCachedHomeContent()?.events?.find { it.id == identifier.id }
            is ContentIdentifier.Serie -> remoteDatasource.getCachedHomeContent()?.series?.find { it.id == identifier.id }
        }

        return content?.let { Result.success(it) }
    }

    override fun findMovieContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> remoteDatasource.getCachedMovies()?.vods?.find { it.getId() == identifier.id }
            is ContentIdentifier.Event -> remoteDatasource.getCachedMovies()?.events?.find { it.getId() == identifier.id }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findDocumentaryContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> remoteDatasource.getCachedDocumentaries()?.vods?.find { it.getId() == identifier.id }
            is ContentIdentifier.Event -> remoteDatasource.getCachedDocumentaries()?.events?.find { it.getId() == identifier.id }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findAdultContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> remoteDatasource.getCachedAdults()?.vods?.find { it.getId() == identifier.id }
            is ContentIdentifier.Event -> remoteDatasource.getCachedAdults()?.events?.find { it.getId() == identifier.id }
            is ContentIdentifier.Channel -> remoteDatasource.getCachedHomeContent()?.channels?.find { it.id == identifier.id }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findSportsContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> remoteDatasource.getCachedSports()?.vods?.find { it.getId() == identifier.id }
            is ContentIdentifier.Event -> remoteDatasource.getCachedSports()?.events?.find { it.getId() == identifier.id }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findKidsContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> remoteDatasource.getCachedKids()?.vods?.find { it.getId() == identifier.id }
            is ContentIdentifier.Event -> remoteDatasource.getCachedKids()?.events?.find { it.getId() == identifier.id }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findWarnerContent(identifier: ContentIdentifier) : Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> {
                remoteDatasource.getCachedWarner()?.vods?.find { it.getId() == identifier.id }
                    ?: remoteDatasource.getCachedWarner()?.featured?.find { it.id == identifier.id }
            }
            else -> null
        }
        return content?.let { Result.success(it) }
    }

    override fun findAcontraContent(identifier: ContentIdentifier) : Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> {
                remoteDatasource.getCachedAcontra()?.vods?.find { it.getId() == identifier.id }
                    ?: remoteDatasource.getCachedAcontra()?.featured?.find { it.id == identifier.id }
            }
            else -> null
        }
        return content?.let { Result.success(it) }
    }

    override fun findAMCContent(identifier: ContentIdentifier) : Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> {
                remoteDatasource.getCachedAMC()?.vods?.find { it.getId() == identifier.id }
                    ?: remoteDatasource.getCachedAMC()?.featured?.find { it.id == identifier.id }
            }
            else -> null
        }
        return content?.let { Result.success(it) }
    }

    override fun findGenreWithId(id: Int): Result<Genre> {
        return runCatching {
            remoteDatasource.getCachedHomeContent()
                ?.genres
                ?.firstOrNull { it.id == id }
                ?: throw NoSuchElementException("Genre with id $id not found")
        }
    }

    override fun findChannelWithId(id: Int): Result<Channel> {
        return runCatching {
            remoteDatasource.getCachedHomeContent()
                ?.channels
                ?.find { it.id == id }
                ?: throw NoSuchElementException("Channel with id $id not found")
        }
    }

    override fun shouldRequestPin(): Boolean {
        // 1. Obtener la última fecha guardada desde el LocalDataSource.
        val lastPinTime: ZonedDateTime? = localDataSource.getLastTimePinWasCorrect()

        // 2. Si nunca se ha guardado una fecha (es nulo), debemos solicitar el PIN.
        if (lastPinTime == null) {
            logger.debug(TAG, "No ZonedDateTime found, PIN request is needed.")
            return true
        }

        // 3. Obtener la hora actual con la misma zona horaria.
        val currentTime = ZonedDateTime.now(lastPinTime.zone)

        // 4. Calcular la duración entre la fecha guardada y la actual.
        val duration = Duration.between(lastPinTime, currentTime)

        // 5. Comparar la duración con 15 minutos.
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

}