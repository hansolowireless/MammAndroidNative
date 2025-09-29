package com.mamm.mammapps.data.model.player.heartbeat

import com.google.gson.annotations.SerializedName

data class HeartBeatRequest(
    @SerializedName("device_type")
    val deviceType: String,

    @SerializedName("device_serial")
    val deviceSerial: String
)
