package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.login.LocatorResponse
import com.mamm.mammapps.data.model.login.LoginResponse
import com.mamm.mammapps.data.datasource.session.SessionDatasource
import com.mamm.mammapps.domain.interfaces.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDatasource,
    private val localDataSource: LocalDataSource,
    private val sessionDataSource: SessionDatasource,
    private val logger: Logger
) : LoginRepository {

    companion object {
        private const val TAG = "LoginRepositoryImpl"
    }

    override suspend fun login(username: String, password: String): Result<LoginResponse> {
        return runCatching {
            remoteDataSource.login(username, password)
        }.onSuccess { response ->
            response.data?.let {
                clearCaches()
                sessionDataSource.saveUserCredentials(
                    username = username,
                    password = password,
                    loginData = response.data
                )
            } ?: throw IllegalStateException("login - response data is null")
        }
    }

    override suspend fun checkLocator(username: String): Result<LocatorResponse> {
        return runCatching {
            remoteDataSource.checkLocator(username)
        }
    }

    override fun setShowBrandedContentMenus(show: Boolean) {
        logger.debug(TAG, "Setting show branded content menus to $show")
        localDataSource.setShowBrandedContentMenus(show)
    }

    override fun getShowBrandedContentMenus(): Result<Boolean> {
        return runCatching {
            localDataSource.getShowBrandedContentMenus() ?: throw IllegalStateException("Show branded content menus is null")
        }
    }

    override fun getUserIsHoreca(): Result<Boolean> {
        return runCatching {
            sessionDataSource.isHoreca()
        }
    }

    override fun getOperatorLogoUrl() : Result<String> {
        return runCatching {
            remoteDataSource.getOperatorLogoUrl() ?: throw IllegalStateException("Operator logo URL is null")
        }
    }

    override suspend fun getCredentials(): Result<Pair<String?, String?>> {
        return runCatching {
            val credentials = sessionDataSource.getUserCredentials()
            val (username, password) = credentials
            if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
                throw IllegalStateException("Invalid credentials: username or password is null/empty")
            }
            username to password
        }
    }

    override fun setSessionToken(newRefresh: String, newAccess: String) {
        sessionDataSource.updateToken(newRefresh, newAccess)
    }

    override fun clearCaches() : Result<Unit> {
        return runCatching {
            sessionDataSource.clear()
            localDataSource.clearCache()
        }
    }
}