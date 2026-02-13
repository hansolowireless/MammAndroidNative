package com.mamm.mammapps.domain.model

data class DownloadSpeedResult(
    val speedMbps: Double,
    val durationMs: Long,
    val bytesDownloaded: Long
)