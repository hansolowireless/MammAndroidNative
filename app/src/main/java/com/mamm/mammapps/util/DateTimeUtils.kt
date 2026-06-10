package com.mamm.mammapps.util

import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

fun String.toZonedDateTimeEPG(): ZonedDateTime? {
    return runCatching {
        val isoString = this.replace(" ", "T") + "Z"
        val instant = Instant.parse(isoString)
        instant.atZone(ZoneOffset.UTC)
    }.getOrNull()
}

fun getCurrentDate(): ZonedDateTime {
    return ZonedDateTime.now()
}
