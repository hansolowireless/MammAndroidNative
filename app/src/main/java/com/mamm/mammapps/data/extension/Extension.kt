package com.mamm.mammapps.data.extension

import com.mamm.mammapps.data.model.GetBrandedContentResponseDto
import com.mamm.mammapps.data.model.GetHomeContentResponseDto
import com.mamm.mammapps.data.model.metadata.MetadataDto
import com.mamm.mammapps.domain.model.entity.Event
import com.mamm.mammapps.ui.extension.adult
import com.mamm.mammapps.util.getCurrentDate
import retrofit2.Response
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.net.URL

fun GetHomeContentResponseDto.transformData(
    channelOrder: Map<Int, Int>? = null,
    userId: String? = null
): GetHomeContentResponseDto = run {
    val transformedContents = contents?.map { content ->
        content.copy(metadata = MetadataDto.fromTbContentItems(content.tbContentItems ?: emptyList()))
    }

    val transformedChannels = channels?.map { channel ->
        channel.copy(
            deliveryURL = channel.deliveryURL
                ?.replace("\${id_channel}", channel.id.toString())
                ?.replace("\${quality}", "HD")
                ?.plus(".smil/"),
            position = channelOrder?.get(channel.id) ?: Int.MAX_VALUE,
            fingerPrintText = userId
        )
    }?.sortedBy { it.position }

    this.copy(
        contents = transformedContents,
        channels = transformedChannels
    )
}

fun GetBrandedContentResponseDto.correctAdultImages(
): GetBrandedContentResponseDto = run {
    val transformedEvents = events?.map {
        it.copy(
            posterLogo = it.posterLogo?.adult(),
        )
    }
    val transformedVods = vods?.map {
        it.copy(
            posterLogo = it.posterLogo?.adult(),
            contentLogo = it.contentLogo?.adult()
        )
    }
    this.copy(
        events = transformedEvents,
        vods = transformedVods
    )
}

fun LocalDate.toEPGRequestDate(): String {
    return this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

fun ZonedDateTime.toTSTVDateString(): String {
    return this.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm"))
}

fun Response<*>.isRedirect(): Boolean {
    return code() in 300..399
}

fun Event.catchupIsAvailable(availableCatchupHours: Int): Boolean {
    val startInstant = startDateTime?.toInstant()
    val nowInstant = getCurrentDate().toInstant()
    val differenceInMinutes = ChronoUnit.MINUTES.between(startInstant, nowInstant)
    val differenceInHours = differenceInMinutes / 60.0

    return availableCatchupHours > 0 &&
            differenceInHours > 0 &&
            differenceInHours < availableCatchupHours
}

fun String.toValidBaseUrl(): String {
    val url = URL(this)
    if ((url.protocol.equals("http", ignoreCase = true) || url.protocol.equals("https", ignoreCase = true)) && !url.host.isNullOrBlank()) {
        return if (this.endsWith("/")) this else "$this/"
    } else {
        throw IllegalArgumentException("URL protocol must be HTTP or HTTPS, and host must not be blank")
    }
}