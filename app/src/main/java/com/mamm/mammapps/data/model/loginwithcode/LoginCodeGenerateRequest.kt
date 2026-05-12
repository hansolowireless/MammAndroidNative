package com.mamm.mammapps.data.model.loginwithcode

import com.google.gson.annotations.SerializedName

data class LoginCodeGenerateRequest(
    @SerializedName("device_type")
    val deviceType: String,
    
    @SerializedName("device_serial")
    val deviceSerial: String
)
