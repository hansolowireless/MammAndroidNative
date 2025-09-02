package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.LocatorResponse
import com.mamm.mammapps.data.model.LoginResponse

interface MammRepository {
    suspend fun login(username: String, password: String): Result<LoginResponse>
    suspend fun checkLocator(username: String): Result<LocatorResponse>
    suspend fun saveUserCredentials(username: String, password: String) : Result<Unit>
    suspend fun getUserCredentials(): Result<Pair<String?, String?>>
    suspend fun getHomeContent(url: String) : Result<GetHomeContentResponse>
}