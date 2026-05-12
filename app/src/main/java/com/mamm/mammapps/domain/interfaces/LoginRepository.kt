package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.data.model.login.LocatorResponse
import com.mamm.mammapps.data.model.login.LoginResponse

interface LoginRepository {
    suspend fun login(username: String, password: String): Result<LoginResponse>
    suspend fun checkLocator(username: String): Result<LocatorResponse>
    fun getOperatorLogoUrl() : Result<String>
    suspend fun getCredentials(): Result<Pair<String?, String?>>
    fun setSessionToken(newRefresh: String, newAccess: String)
    fun setShowBrandedContentMenus(show: Boolean)
    fun getShowBrandedContentMenus(): Result<Boolean>
    fun getUserIsHoreca() : Result<Boolean>
    fun clearCaches() : Result<Unit>
}