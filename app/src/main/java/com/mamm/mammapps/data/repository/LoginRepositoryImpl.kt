package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.local.SecurePreferencesManager
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.login.LocatorResponse
import com.mamm.mammapps.data.model.login.LoginResponse
import com.mamm.mammapps.data.session.SessionManager
import com.mamm.mammapps.domain.interfaces.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val remoteDatasource: RemoteDatasource,
    private val localDataSource: LocalDataSource,
    private val sessionManager: SessionManager,
    private val securePreferencesManager: SecurePreferencesManager,
    private val logger: Logger
) : LoginRepository {

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

    override suspend fun saveUserCredentials(username: String, password: String): Result<Unit> {
        return runCatching {
            localDataSource.saveUserCredentials(username, password)
        }
    }

    override fun getOperatorLogoUrl() : Result<String> {
        return runCatching {
            remoteDatasource.getOperatorLogoUrl() ?: throw IllegalStateException("Operator logo URL is null")
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

    override fun logout() : Result<Unit> {
        return runCatching {
            sessionManager.clear()
            remoteDatasource.clearCache()
            securePreferencesManager.clearCredentials()
        }
    }
}