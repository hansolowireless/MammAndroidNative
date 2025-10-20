package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.data.model.login.LocatorResponse
import com.mamm.mammapps.data.model.login.LoginResponse

interface LoginRepository {
    suspend fun login(username: String, password: String): Result<LoginResponse>
    suspend fun checkLocator(username: String): Result<LocatorResponse>
    fun getOperatorLogoUrl() : Result<String>
    suspend fun saveUserCredentials(username: String, password: String) : Result<Unit>
    suspend fun getUserCredentials(): Result<Pair<String?, String?>>
    fun logout() : Result<Unit>
}