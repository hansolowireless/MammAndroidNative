package com.mamm.mammapps.domain.model.player

data class QosData(
    val deviceType: String? = null,
    val playerBw: String,
    val activeTrack: String,
    val videoBw: String,
    val bufTime: String,
    val loadLatency: String,
    val playTime: String,
    val primaryNode: String,
    val id: String,
    val type: String,
    val ip: String? = null,
    val adId: String? = null
)
