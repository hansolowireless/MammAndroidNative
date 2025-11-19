package com.mamm.mammapps.data.model.player

import com.google.gson.annotations.SerializedName

data class QosData(
    @SerializedName("deviceType")
    val deviceType: String? = null,
    @SerializedName("playerBw")
    val playerBw: String,
    @SerializedName("activeTrack")
    val activeTrack: String,
    @SerializedName("videoBw")
    val videoBw: String,
    @SerializedName("bufTime")
    val bufTime: String,
    @SerializedName("loadLatency")
    val loadLatency: String,
    @SerializedName("playTime")
    val playTime: String,
    @SerializedName("primaryNode")
    val primaryNode: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("ip")
    val ip: String? = null,
    @SerializedName("ad_id")
    val adId: String? = null
)