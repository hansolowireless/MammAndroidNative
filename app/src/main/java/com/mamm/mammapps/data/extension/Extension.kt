package com.mamm.mammapps.data.extension

import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.metadata.Metadata
import retrofit2.Response
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun GetHomeContentResponse.transformData(
    channelOrder: Map<Int, Int>? = null
): GetHomeContentResponse = run {
    val transformedContents = contents?.map { content ->
        content.copy(metadata = Metadata.fromTbContentItems(content.tbContentItems ?: emptyList()))
    }

    val transformedChannels = channels?.map { channel ->
        channel.copy(
            deliveryURL = channel.deliveryURL
                ?.replace("\${id_channel}", channel.id.toString())
                ?.replace("\${quality}", "HD")
                ?.plus(".smil/"),
            position = channelOrder?.get(channel.id) ?: 0
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

fun String.toZonedDateTimeEPG(): ZonedDateTime {
    val isoString = this.replace(" ", "T") + "Z"
    val instant = Instant.parse(isoString)
    return instant.atZone(ZoneOffset.UTC)
}

fun Response<*>.isRedirect(): Boolean {
    return code() in 300..399
}