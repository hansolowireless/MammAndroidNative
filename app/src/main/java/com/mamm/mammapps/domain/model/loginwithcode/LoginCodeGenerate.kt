package com.mamm.mammapps.domain.model.loginwithcode

data class LoginCodeGenerate(
    val code: String,
    val expiresIn: Int,
    val pollingInterval: Int
)
