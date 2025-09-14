package com.mamm.mammapps.data.model.player

data class QoSData(
    val playerBw: String,
    val activeTrack: String,
    val videoBw: String,
    val bufTime: String,
    val loadLatency: String,
    val playTime: String,
    val primaryNode: String,
    val id: String,
    val type: String
)