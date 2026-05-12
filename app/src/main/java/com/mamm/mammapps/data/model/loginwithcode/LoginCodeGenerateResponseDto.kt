package com.mamm.mammapps.data.model.loginwithcode

import com.google.gson.annotations.SerializedName

data class LoginCodeGenerateResponseDto(
    @SerializedName("code")
    val code: String,
    
    @SerializedName("expiresIn")
    val expiresIn: Int,
    
    @SerializedName("pollingInterval")
    val pollingInterval: Int
)
