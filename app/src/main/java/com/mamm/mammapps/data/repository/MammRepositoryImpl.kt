package com.mamm.mammapps.data.repository

import androidx.core.net.toUri
import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.extension.transformData
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.Genre
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.data.model.login.LocatorResponse
import com.mamm.mammapps.data.model.login.LoginResponse
import com.mamm.mammapps.data.session.SessionManager
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.model.ContentIdentifier
import javax.inject.Inject

class MammRepositoryImpl @Inject constructor (
    private val remoteDatasource: RemoteDatasource,
    private val localDataSource: LocalDataSource,
    private val sessionManager: SessionManager,
    private val logger: Logger
) : MammRepository {

    companion object {
        private const val TAG = "MammRepositoryImpl"
    }

    override suspend fun login(username: String, password: String): Result<LoginResponse> {
        return runCatching {
            remoteDatasource.login(username, password)
        }
    }

    override suspend fun checkLocator(username: String): Result<LocatorResponse> {
        return runCatching {
            remoteDatasource.checkLocator(username)
        }
    }

    override suspend fun saveUserCredentials(username: String, password: String) : Result<Unit> {
        return runCatching {
            localDataSource.saveUserCredentials(username, password)
        }
    }

    override suspend fun getUserCredentials(): Result<Pair<String?, String?>> {
        return runCatching {
            val credentials = localDataSource.getUserCredentials()
            val (username, password) = credentials
            if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
                throw IllegalStateException("Invalid credentials: username or password is null/empty")
            }
            username to password
        }
    }

    override suspend fun getHomeContent() : Result<GetHomeContentResponse> {
        return runCatching {
            remoteDatasource.getHomeContent().transformData(sessionManager.channelOrder)
        }.onSuccess { response ->
            logger.debug(TAG, "getHomeContent Received and saved successful response")
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

    override suspend fun getAdults(): Result<GetOtherContentResponse> {
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
            is ContentIdentifier.VoD -> remoteDatasource.getCachedMovies()?.vods?.find { it.id == identifier.id }
            is ContentIdentifier.Event -> remoteDatasource.getCachedMovies()?.events?.find { it.id == identifier.id }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findDocumentaryContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> remoteDatasource.getCachedDocumentaries()?.vods?.find { it.id == identifier.id }
            is ContentIdentifier.Event -> remoteDatasource.getCachedDocumentaries()?.events?.find { it.id == identifier.id }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findAdultContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> remoteDatasource.getCachedAdults()?.vods?.find { it.id == identifier.id }
            is ContentIdentifier.Event -> remoteDatasource.getCachedAdults()?.events?.find { it.id == identifier.id }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findSportsContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> remoteDatasource.getCachedSports()?.vods?.find { it.id == identifier.id }
            is ContentIdentifier.Event -> remoteDatasource.getCachedSports()?.events?.find { it.id == identifier.id }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override fun findKidsContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> remoteDatasource.getCachedKids()?.vods?.find { it.id == identifier.id }
            is ContentIdentifier.Event -> remoteDatasource.getCachedKids()?.events?.find { it.id == identifier.id }
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
                ?: throw NoSuchElementException("Channel with id $id not found")}
    }

}