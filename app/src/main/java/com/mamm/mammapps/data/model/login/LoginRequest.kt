package com.mamm.mammapps.data.model.login

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("user")
    val user: String,

    @SerializedName("pass")
    val pass: String,

    @SerializedName("device_type")
    val deviceType: String,

    @SerializedName("device_serial")
    val deviceSerial: String
)