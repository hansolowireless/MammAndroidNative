package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.mapper.toDomain
import com.mamm.mammapps.data.config.Config
import com.mamm.mammapps.data.model.login.LocatorResponse
import com.mamm.mammapps.data.model.login.LoginResponse
import com.mamm.mammapps.data.model.session.RefreshTokenRequest
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
            updateLocatorConfiguration(username)
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

    override suspend fun autoLogin(): Result<Unit> {
        return runCatching {
            val username = sessionDataSource.getUserCredentials().first
            
            updateLocatorConfiguration(username)

            val refreshToken = sessionDataSource.refreshToken
            if (!refreshToken.isNullOrEmpty()) {
                try {
                    val response = remoteDataSource.refreshToken(RefreshTokenRequest(refreshToken))
                    response.data?.let { newLoginData ->
                        sessionDataSource.updateLoginData(newLoginData)
                        return@runCatching
                    }
                } catch (e: Exception) {
                    logger.debug(TAG, "Refresh token failed, falling back to credentials: ${e.message}")
                }
            }
            
            val password = sessionDataSource.getUserCredentials().second
            if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
                throw IllegalStateException("No valid refresh token or stored credentials")
            }
            
            val response = remoteDataSource.login(username, password)
            response.data?.let {
                clearCaches()
                sessionDataSource.saveUserCredentials(
                    username = username,
                    password = password,
                    loginData = it
                )
            } ?: throw IllegalStateException("login response data is null")
        }
    }

    override suspend fun checkLocator(username: String): Result<LocatorResponse> {
        return runCatching {
            remoteDataSource.checkLocator(username)
        }
    }

    private suspend fun updateLocatorConfiguration(username: String?) {
        if (!username.isNullOrEmpty() && Config.shouldUseDynamicUrls) {
            try {
                val locatorResponse = remoteDataSource.checkLocator(username)
                logger.debug(TAG, "updateLocatorConfiguration Received locator response: $locatorResponse")
                Config.updateDynamicUrls(locatorResponse)
                setShowBrandedContentMenus(false)
            } catch (e: Exception) {
                logger.error(TAG, "updateLocatorConfiguration Locator request failed: ${e.message}")
                Config.resetDynamicUrls()
                setShowBrandedContentMenus(true)
            }
        } else {
            setShowBrandedContentMenus(true)
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

    override fun clearCaches() : Result<Unit> {
        return runCatching {
            sessionDataSource.clear()
            localDataSource.clearCache()
        }
    }
}