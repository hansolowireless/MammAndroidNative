package com.mamm.mammapps.domain.model.about

data class DownloadSpeedResult(
    val speedMbps: Double,
    val durationMs: Long,
    val bytesDownloaded: Long
)