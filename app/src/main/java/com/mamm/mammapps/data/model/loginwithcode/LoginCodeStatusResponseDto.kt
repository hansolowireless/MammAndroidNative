package com.mamm.mammapps.data.model.loginwithcode

import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.login.LoginDataDto

data class LoginCodeStatusResponseDto(
    @SerializedName("result")
    val result: String?,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("data")
    val data: LoginDataDto?
)
