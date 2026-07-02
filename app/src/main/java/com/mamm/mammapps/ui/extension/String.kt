package com.mamm.mammapps.ui.extension

import android.content.Context
import coil.request.ImageRequest
import com.google.android.exoplayer2.util.MimeTypes
import com.mamm.mammapps.data.logger.SimpleLogger
import com.mamm.mammapps.ui.constant.PlayerConstant
import kotlin.math.floor

fun String.squared() = this.replace(".png", "_x4.png").replace(".jpg", "_x4.jpg")

fun String.landscape() = this.replace(".png", "_viewer.png").replace(".jpg", "_viewer.jpg")

fun String.adult(): String {
    return this.replace(".png", "_p.png").replace(".jpg", "_p.jpg")
}

fun String.buildThumbnailUrl(position: Long?): String {
    requireNotNull(position) { "buildThumbnailUrl position cannot be null" }

    val contentID = when {
        contains("smil:") -> substringAfter("smil:").substringBefore("_")
        contains("nopack03-") -> substringAfter("nopack03-").substringBefore("/")
        contains("nopack04-") -> substringAfter("nopack04-").substringBefore("/")
        else -> substringAfter("nopack-").substringBefore("/")
    }

    val thumbnailNumber =
        (floor(position / PlayerConstant.THUMBNAIL_UPDATE_INTERVAL.toDouble()) + 1).toInt()
    val thumbnailNumberString = thumbnailNumber.toString().padStart(3, '0')

    val baseUrl = when {
        contains("smil:") -> substringBefore("/smil:")
        contains("nopack03-") -> substringBefore("/nopack03-")
        contains("nopack04-") -> substringBefore("/nopack04-")
        else -> substringBefore("/nopack-")
    }
    val thumbnail = "$baseUrl-img/${contentID}_mf$thumbnailNumberString.jpg"
    SimpleLogger().debug("buildThumbnailUrl", "thumbnail: $thumbnail")
    return "$baseUrl-img/${contentID}_mf$thumbnailNumberString.jpg"
}

fun String.fadeInImageRequest(context: Context): ImageRequest {
    return ImageRequest.Builder(context)
        .data(this)
        .crossfade(true)
        .crossfade(300)
        .build()
}

fun String.inferMimeType(): String {
    return if (this.contains(PlayerConstant.M3U8_EXTENSION, ignoreCase = true)) {
        MimeTypes.APPLICATION_M3U8
    } else {
        MimeTypes.APPLICATION_MPD
    }
}

fun String?.ifNullOrBlank(default: () -> String): String =
    if (isNullOrBlank()) default() else this!!
