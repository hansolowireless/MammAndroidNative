package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.LocatorResponse
import com.mamm.mammapps.data.model.LoginData
import com.mamm.mammapps.data.model.LoginResponse
import com.mamm.mammapps.domain.interfaces.MammRepository
import javax.inject.Inject

class MammRepositoryImpl @Inject constructor (
    private val remoteDatasource: RemoteDatasource,
    private val localDataSource: LocalDataSource
) : MammRepository {

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
            remoteDatasource.getHomeContent(url)
        }
    }

}