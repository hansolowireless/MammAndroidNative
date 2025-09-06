package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.extension.transformData
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.LocatorResponse
import com.mamm.mammapps.data.model.LoginResponse
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.model.ContentIdentifier
import javax.inject.Inject

class MammRepositoryImpl @Inject constructor (
    private val remoteDatasource: RemoteDatasource,
    private val localDataSource: LocalDataSource,
    private val logger: Logger
) : MammRepository {

    companion object {
        private const val TAG = "MammRepositoryImpl"
    }

    private var homeContent: GetHomeContentResponse? = null

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

    override suspend fun getHomeContent(url: String) : Result<GetHomeContentResponse> {
        return runCatching {
            remoteDatasource.getHomeContent(url).transformData()
        }.onSuccess { response ->
            logger.debug(TAG, "getHomeContent Received and saved successful response")
            homeContent = response
        }
    }

    override fun getCachedHomeContent(): GetHomeContentResponse? {
        return homeContent
    }

    override fun findContent(identifier: ContentIdentifier): Result<Any>? {
        val content: Any? = when (identifier) {
            is ContentIdentifier.Channel -> homeContent?.channels?.find { it.id.toString() == identifier.id }
            is ContentIdentifier.VoD -> homeContent?.contents?.find { it.id.toString() == identifier.id }
            is ContentIdentifier.Event -> homeContent?.events?.find { it.id.toString() == identifier.id }
            is ContentIdentifier.Serie -> homeContent?.series?.find { it.id.toString() == identifier.id }
        }

        return content?.let { Result.success(it) }
    }

}