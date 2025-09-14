package com.mamm.mammapps.ui.model.player

import java.time.ZonedDateTime

data class LiveEventInfoUI (
    val title: String,
    val logoURL: String,
    val begin: ZonedDateTime,
    val end: ZonedDateTime
)