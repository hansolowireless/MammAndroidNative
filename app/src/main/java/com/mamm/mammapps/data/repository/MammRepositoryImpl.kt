package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.extension.transformData
import com.mamm.mammapps.data.logger.Logger
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

    override suspend fun getMovies(jsonParam: String): Result<GetOtherContentResponse> {
        return runCatching {
            remoteDatasource.getMovies(jsonParam)
        }.onSuccess { response ->
            logger.debug(TAG, "getMovies Received and saved successful response")
        }
    }

    override fun findHomeContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.Channel -> remoteDatasource.getCachedHomeContent()?.channels?.find { it.id.toString() == identifier.id }
            is ContentIdentifier.VoD -> remoteDatasource.getCachedHomeContent()?.contents?.find { it.id.toString() == identifier.id }
            is ContentIdentifier.Event -> remoteDatasource.getCachedHomeContent()?.events?.find { it.id.toString() == identifier.id }
            is ContentIdentifier.Serie -> remoteDatasource.getCachedHomeContent()?.series?.find { it.id.toString() == identifier.id }
        }

        return content?.let { Result.success(it) }
    }

    override fun findMovieContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.VoD -> remoteDatasource.getCachedMovies()?.vods?.find { it.idEvent == identifier.id }
            is ContentIdentifier.Event -> remoteDatasource.getCachedMovies()?.events?.find { it.idEvent == identifier.id }
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

}