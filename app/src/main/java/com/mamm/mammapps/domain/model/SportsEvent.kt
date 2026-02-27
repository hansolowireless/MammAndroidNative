package com.mamm.mammapps.domain.model

import java.time.ZonedDateTime

data class SportsEvent(
    val title: String,
    val description: String,
    val startTime: ZonedDateTime?,
    val endTime: ZonedDateTime?,
    val channelId: String,
    val horizontalImage: String,
    val verticalImage: String
)
