package com.mamm.mammapps.data.repository

import androidx.core.net.toUri
import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.mapper.toDomain
import com.mamm.mammapps.domain.model.exception.GetMemoriesException
import com.mamm.mammapps.data.datasource.session.SessionDatasource
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.domain.model.BrandedContent
import com.mamm.mammapps.domain.model.entity.Channel
import com.mamm.mammapps.domain.model.Genre
import com.mamm.mammapps.domain.model.HomeContent
import com.mamm.mammapps.domain.model.OtherContent
import com.mamm.mammapps.domain.model.Subgenre
import com.mamm.mammapps.domain.model.memories.Memories
import com.mamm.mammapps.domain.model.serie.SerieInfo
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

    override suspend fun getHomeContent(): Result<HomeContent> {
        return runCatching {
            localDataSource.getHomeContent()?.let { return@runCatching it.toDomain() }
            val response = remoteDatasource.getHomeContent()
            localDataSource.setHomeContent(response)
            localDataSource.setCachedSubgenreList(
                response.genres?.flatMap { genre ->
                    genre.subgenres ?: emptyList()
                } ?: emptyList()
            )
            response.toDomain()
        }.onSuccess {
            logger.debug(TAG, "getHomeContent Received and saved successful response")
        }.onFailure {
            logger.error(TAG, "getHomeContent Failed: ${it}")
        }
    }

    override suspend fun getMovies(): Result<OtherContent> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))

        return runCatching {
            localDataSource.getMoviesContent()?.let { return@runCatching it.toDomain() }
            val response = remoteDatasource.getMovies(jsonParam)
            localDataSource.setMoviesContent(response)
            response.toDomain()
        }.onSuccess {
            logger.debug(TAG, "getMovies Received and saved successful response")
        }
    }

    override suspend fun getDocumentaries(): Result<OtherContent> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))

        return runCatching {
            localDataSource.getDocumentariesContent()?.let { return@runCatching it.toDomain() }
            val response = remoteDatasource.getDocumentaries(jsonParam)
            localDataSource.setDocumentariesContent(response)
            response.toDomain()
        }.onSuccess {
            logger.debug(TAG, "getDocumentaries Received and saved successful response")
        }
    }

    override suspend fun getAdults(): Result<BrandedContent> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))

        return runCatching {
            localDataSource.getAdultsContent()?.let { return@runCatching it.toDomain() }
            val response = remoteDatasource.getAdults(jsonParam)
            localDataSource.setAdultsContent(response)
            response.toDomain()
        }.onSuccess {
            logger.debug(TAG, "getAdults Received and saved successful response")
        }
    }

    override suspend fun getKids(): Result<OtherContent> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))

        return runCatching {
            localDataSource.getKidsContent()?.let { return@runCatching it.toDomain() }
            val response = remoteDatasource.getKids(jsonParam)
            localDataSource.setKidsContent(response)
            response.toDomain()
        }.onSuccess {
            logger.debug(TAG, "getKids Received and saved successful response")
        }
    }

    override suspend fun getSports(): Result<OtherContent> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))

        return runCatching {
            localDataSource.getSportsContent()?.let { return@runCatching it.toDomain() }
            val response = remoteDatasource.getSports(jsonParam)
            localDataSource.setSportsContent(response)
            response.toDomain()
        }.onSuccess {
            logger.debug(TAG, "getSports Received and saved successful response")
        }
    }

    override suspend fun getWarner(): Result<BrandedContent> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))
        return runCatching {
            localDataSource.getWarnerContent()?.let { return@runCatching it.toDomain() }
            val response = remoteDatasource.getWarner(jsonParam)
            localDataSource.setWarnerContent(response)
            response.toDomain()
        }.onSuccess {
            logger.debug(TAG, "getWarner Received and saved successful response")
        }
    }

    override suspend fun getAcontra(): Result<BrandedContent> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))
        return runCatching {
            localDataSource.getAcontraContent()?.let { return@runCatching it.toDomain() }
            val response = remoteDatasource.getAcontra(jsonParam)
            localDataSource.setAcontraContent(response)
            response.toDomain()
        }.onSuccess {
            logger.debug(TAG, "getAcontra Received and saved successful response")
        }
    }

    override suspend fun getAMC(): Result<BrandedContent> {
        val jsonParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))
        return runCatching {
            localDataSource.getAMCContent()?.let { return@runCatching it.toDomain() }
            val response = remoteDatasource.getAMC(jsonParam)
            localDataSource.setAMCContent(response)
            response.toDomain()
        }.onSuccess {
            logger.debug(TAG, "getAMC Received and saved successful response")
        }
    }

    override suspend fun getMemories(): Result<Memories> {
        return runCatching {
            localDataSource.getMyMemories()?.let { return@runCatching it.toDomain() }
            val response = remoteDatasource.getMyMemories()
            if (response.sections.isNullOrEmpty()) {
                throw GetMemoriesException.EmptyList
            }
            localDataSource.setMyMemories(response)
            response.toDomain()
        }.onFailure {
            logger.error(TAG, "getMemories failed: ${it.message}, $it")
        }
    }

    override suspend fun getSeasonsInfo(serieId: Int): Result<SerieInfo> {
        return runCatching {
            remoteDatasource.getSeasonInfo(serieId).toDomain()
        }.onSuccess {
            logger.debug(TAG, "getSeasonsInfo Received successful response")
        }
    }

    override suspend fun getExpandedCategoryContent(categoryId: Int): Result<BrandedContent> {
        return runCatching {
            remoteDatasource.getExpandedCategory(categoryId).toDomain()
        }.onSuccess {
            logger.debug(TAG, "getExpandedCategory Received successful response")
        }
    }

    override fun findHomeContent(identifier: ContentIdentifier): Result<Any>? {
        var content: Any? = when (identifier) {
            is ContentIdentifier.Channel -> localDataSource.getHomeContent()?.channels?.find { it.id == identifier.id }?.toDomain()
            is ContentIdentifier.VoD -> localDataSource.getHomeContent()?.contents?.find { it.id == identifier.id }?.toDomain()
            is ContentIdentifier.Event -> localDataSource.getHomeContent()?.events?.find { it.id == identifier.id }?.toDomain()
            is ContentIdentifier.Serie -> localDataSource.getHomeContent()?.series?.find { it.id == identifier.id }?.toDomain()
        }

        if (content == null) {
            content = localDataSource.getHomeContent()?.featured?.find { it.id == identifier.id }?.toDomain()
        }

        return content?.let { Result.success(it) }
    }

    override fun findMovieContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> localDataSource.getMoviesContent()?.vods?.find { it.getId() == identifier.id }?.toDomain()
            is ContentIdentifier.Event -> localDataSource.getMoviesContent()?.events?.find { it.getId() == identifier.id }?.toDomain()
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findDocumentaryContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> localDataSource.getDocumentariesContent()?.vods?.find { it.getId() == identifier.id }?.toDomain()
            is ContentIdentifier.Event -> localDataSource.getDocumentariesContent()?.events?.find { it.getId() == identifier.id }?.toDomain()
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findAdultContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> localDataSource.getAdultsContent()?.vods?.find { it.getId() == identifier.id }?.toDomain()
            is ContentIdentifier.Event -> localDataSource.getAdultsContent()?.events?.find { it.getId() == identifier.id }?.toDomain()
            is ContentIdentifier.Channel -> localDataSource.getHomeContent()?.channels?.find { it.id == identifier.id }?.toDomain()
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findSportsContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> localDataSource.getSportsContent()?.vods?.find { it.getId() == identifier.id }?.toDomain()
            is ContentIdentifier.Event -> localDataSource.getSportsContent()?.events?.find { it.getId() == identifier.id }?.toDomain()
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findKidsContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> localDataSource.getKidsContent()?.vods?.find { it.getId() == identifier.id }?.toDomain()
            is ContentIdentifier.Event -> localDataSource.getKidsContent()?.events?.find { it.getId() == identifier.id }?.toDomain()
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findWarnerContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> {
                localDataSource.getWarnerContent()?.vods?.find { it.getId() == identifier.id }?.toDomain()
                    ?: localDataSource.getWarnerContent()?.featured?.find { it.formatId == identifier.id.toString() }?.toDomain()
            }
            else -> null
        }
        return content?.let { Result.success(it) }
    }

    override fun findAcontraContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> {
                localDataSource.getAcontraContent()?.vods?.find { it.getId() == identifier.id }?.toDomain()
                    ?: localDataSource.getAcontraContent()?.featured?.find { it.formatId == identifier.id.toString() }?.toDomain()
            }
            else -> null
        }
        return content?.let { Result.success(it) }
    }

    override fun findAMCContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> {
                localDataSource.getAMCContent()?.vods?.find { it.getId() == identifier.id }?.toDomain()
                    ?: localDataSource.getAMCContent()?.featured?.find { it.formatId == identifier.id.toString() }?.toDomain()
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
                    ?.toDomain()
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
                ?.toDomain()
                ?: throw NoSuchElementException("Genre with id $id not found")
        }
    }

    override fun findChannelWithId(id: Int): Result<Channel> {
        return runCatching {
            localDataSource.getHomeContent()
                ?.channels
                ?.find { it.id == id }
                ?.toDomain()
                ?: throw NoSuchElementException("Channel with id $id not found")
        }
    }

    override fun shouldRequestPin(): Boolean {
        if (sessionManager.pinParental.isNullOrBlank()) {
            logger.info(TAG, "No PIN found for User, No PIN request is needed.")
            return false
        }

        val lastPinTime: ZonedDateTime? = localDataSource.getLastTimePinWasCorrect()

        if (lastPinTime == null) {
            logger.debug(TAG, "No ZonedDateTime found, PIN request is needed.")
            return true
        }

        val currentTime = ZonedDateTime.now(lastPinTime.zone)
        val duration = Duration.between(lastPinTime, currentTime)
        val shouldRequest = duration.toMinutes() > AppConstants.PIN_REQUEST_MINS

        if (shouldRequest) {
            logger.debug(TAG, "More than 15 minutes have passed, PIN request is needed.")
        } else {
            logger.debug(TAG, "Less than 15 minutes have passed, no PIN request needed.")
        }

        return shouldRequest
    }

    override fun validatePin(pin: String): Boolean {
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
        val currentTime = ZonedDateTime.now()
        localDataSource.setLastTimePinWasCorrect(currentTime)
        logger.debug(TAG, "Saved new PIN success timestamp: $currentTime")
    }

    override fun getSubgenreList(): Result<List<Subgenre>> {
        return runCatching {
            localDataSource.getCachedSubgenreList()
                ?.map { it.toDomain() }
                ?: throw NoSuchElementException("Subgenre list not found in cache or is null")
        }
    }
}