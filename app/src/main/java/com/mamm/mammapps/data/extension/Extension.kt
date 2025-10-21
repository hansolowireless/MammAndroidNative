package com.mamm.mammapps.data.extension

import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.metadata.Metadata
import com.mamm.mammapps.data.model.section.EPGEvent
import retrofit2.Response
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun GetHomeContentResponse.transformData(
    channelOrder: Map<Int, Int>? = null,
    userId: String? = null
): GetHomeContentResponse = run {
    val transformedContents = contents?.map { content ->
        content.copy(metadata = Metadata.fromTbContentItems(content.tbContentItems ?: emptyList()))
    }

    var transformedChannels = channels?.map { channel ->
        channel.copy(
            deliveryURL = channel.deliveryURL
                ?.replace("\${id_channel}", channel.id.toString())
                ?.replace("\${quality}", "HD")
                ?.plus(".smil/"),
            position = channelOrder?.get(channel.id) ?: 0,
            fingerPrintText = userId
        )
    }?.sortedBy { it.position }

    this.copy(
        contents = transformedContents,
        channels = transformedChannels
    )
}

fun LocalDate.toEPGRequestDate () : String {
    return this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

fun ZonedDateTime.toTSTVDateString(): String {
    return this.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm"))
}

fun getCurrentDate() : ZonedDateTime {
    return ZonedDateTime.now()
}

fun String.toZonedDateTimeEPG(): ZonedDateTime? {
    return runCatching {
        val isoString = this.replace(" ", "T") + "Z"
        val instant = Instant.parse(isoString)
        instant.atZone(ZoneOffset.UTC)
    }.getOrNull()
}

fun Response<*>.isRedirect(): Boolean {
    return code() in 300..399
}

fun EPGEvent.catchupIsAvailable(availableCatchupHours: Int): Boolean {
    val startInstant = startDateTime?.toInstant()
    val nowInstant = getCurrentDate().toInstant()
    val differenceInMinutes = ChronoUnit.MINUTES.between(startInstant, nowInstant)
    val differenceInHours = differenceInMinutes / 60.0

    return availableCatchupHours > 0 &&
            differenceInHours > 0 &&
            differenceInHours < availableCatchupHours
}