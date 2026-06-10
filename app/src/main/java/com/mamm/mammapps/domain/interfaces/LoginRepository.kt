package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.domain.model.loginwithcode.LoginCodeGenerate
import com.mamm.mammapps.domain.model.loginwithcode.LoginCodeStatus

interface LoginRepository {
    suspend fun generateLoginCode(): Result<LoginCodeGenerate>
    suspend fun checkLoginCodeStatus(code: String): Result<LoginCodeStatus>
    suspend fun authLoginCode(code: String): Result<Unit>
    suspend fun autoLogin(): Result<Unit>
    suspend fun login(username: String, password: String): Result<Unit>
    fun getOperatorLogoUrl() : Result<String>
    fun setShowBrandedContentMenus(show: Boolean)
    fun getShowBrandedContentMenus(): Result<Boolean>
    fun getUserIsHoreca() : Result<Boolean>
    fun clearCaches() : Result<Unit>
}