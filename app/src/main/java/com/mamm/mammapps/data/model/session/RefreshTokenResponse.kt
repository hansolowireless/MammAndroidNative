package com.mamm.mammapps.data.model.session

import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.login.LoginData

data class RefreshTokenResponse(
    @SerializedName("data")
    val data: LoginData?
)
