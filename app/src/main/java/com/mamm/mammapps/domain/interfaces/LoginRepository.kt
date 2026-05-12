package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.data.model.login.LocatorResponse
import com.mamm.mammapps.data.model.login.LoginResponse

import com.mamm.mammapps.domain.model.loginwithcode.LoginCodeGenerate
import com.mamm.mammapps.domain.model.loginwithcode.LoginCodeStatus

interface LoginRepository {
    suspend fun generateLoginCode(): Result<LoginCodeGenerate>
    suspend fun checkLoginCodeStatus(code: String): Result<LoginCodeStatus>
    suspend fun authLoginCode(code: String): Result<Unit>
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