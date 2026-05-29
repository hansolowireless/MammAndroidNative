package com.mamm.mammapps.data.model.loginwithcode

import com.google.gson.annotations.SerializedName

data class AuthLoginCodeRequest(
    @SerializedName("code") val code: String,
    @SerializedName("login") val login: String
)
