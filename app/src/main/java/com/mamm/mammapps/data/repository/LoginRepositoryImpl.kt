package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.mapper.toDomain
import com.mamm.mammapps.data.model.login.LocatorResponse
import com.mamm.mammapps.data.model.login.LoginResponse
import com.mamm.mammapps.data.datasource.session.SessionDatasource
import com.mamm.mammapps.domain.interfaces.LoginRepository
import com.mamm.mammapps.domain.model.loginwithcode.LoginCodeGenerate
import com.mamm.mammapps.domain.model.loginwithcode.LoginCodeStatus
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

    override suspend fun generateLoginCode(): Result<LoginCodeGenerate> {
        return runCatching {
            remoteDataSource.generateLoginCode().toDomain()
        }
    }

    override suspend fun checkLoginCodeStatus(code: String): Result<LoginCodeStatus> {
        return runCatching {
            remoteDataSource.checkTvCodeStatus(code).let { status ->
                val loginData = status.data
                if (loginData != null && loginData.loginUser != null) {
                    logger.debug(TAG, "checkLoginCodeStatus - guardamos credenciales tras PIN correcto")
                    clearCaches()
                    sessionDataSource.saveUserCredentials(
                        username = loginData.loginUser,
                        loginData = loginData
                    )
                } else {
                    logger.debug(TAG, "checkLoginCodeStatus - No se guardaron credenciales porque username o loginData son null")
                }
                status.toDomain()
            }
        }
    }

    override suspend fun authLoginCode(code: String): Result<Unit> {
        return runCatching {
            remoteDataSource.authLoginCode(code)
        }
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