package com.mamm.mammapps.domain.model

import java.util.Date

data class SportsEvent(
    val title: String,
    val description: String,
    val startTime: Date?,
    val endTime: Date?,
    val channelId: String,
    val images: List<String>
)
