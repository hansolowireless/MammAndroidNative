package com.mamm.mammapps.data.mapper

import com.mamm.mammapps.data.model.bookmark.BookmarkDto
import com.mamm.mammapps.data.model.bookmark.RecommendedDto
import com.mamm.mammapps.data.model.mostwatched.MostWatchedContentDto
import com.mamm.mammapps.data.model.recommended.GetRecommendedResponseDto
import com.mamm.mammapps.domain.model.bookmark.Bookmark
import com.mamm.mammapps.domain.model.entity.VoD
import com.mamm.mammapps.domain.model.entity.Event
import com.mamm.mammapps.domain.model.recommended.RecommendedContent

fun BookmarkDto.toDomain(): Bookmark {
    return Bookmark(
        type = this.type,
        id = this.id,
        title = this.title,
        shortDesc = this.shortDesc,
        longDesc = this.longDesc,
        duration = this.duration,
        startDate = this.startDate,
        expiryDate = this.expiryDate,
        logoURL = this.logoURL,
        posterLogo = this.posterLogo,
        deliveryURL = this.deliveryURL,
        channelId = this.channelId,
        currentTime = this.currentTime,
        fcStored = this.fcStored,
        subgenreById = this.subgenreById,
        fcIni = this.fcIni,
        fcEnd = this.fcEnd,
        metadata = this.getMetadata().toDomain()
    )
}

fun RecommendedDto.toVoD(): VoD {
    return VoD(
        id = this.id,
        title = this.title ?: "",
        shortDesc = this.shortDesc ?: "",
        longDesc = this.longDesc ?: "",
        duration = this.duration?.toIntOrNull(),
        deliveryURL = this.deliveryURL,
        logoURL = this.logoURL,
        posterURL = this.posterLogo,
        subgenreById = this.subgenreById,
        metadata = this.getMetadata().toDomain(),
        rawDescription = this.longDesc ?: this.shortDesc ?: "",
        shortDescription = this.shortDesc ?: ""
    )
}

fun RecommendedDto.toEvent(): Event {
    return Event(
        id = this.id,
        title = this.title ?: "",
        description = this.longDesc ?: this.shortDesc ?: "",
        fcIni = this.fcIni,
        fcEnd = this.fcEnd,
        duration = this.duration?.toIntOrNull(),
        deliveryURL = this.deliveryURL,
        logoURL = this.logoURL,
        posterLogo = this.posterLogo,
        channelById = this.channelId?.toIntOrNull(),
        subgenreById = this.subgenreById,
        metadata = this.getMetadata().toDomain()
    )
}

fun RecommendedDto.toDomain(): Any {
    return if (this.type == "vod") {
        this.toVoD()
    } else {
        this.toEvent()
    }
}

fun MostWatchedContentDto.toVoD(): VoD {
    return VoD(
        id = this.id,
        title = this.title ?: "",
        shortDesc = this.shortDesc ?: "",
        longDesc = this.longDesc ?: "",
        duration = this.duration,
        deliveryURL = this.deliveryURL,
        logoURL = this.logoURL,
        posterURL = this.posterLogo,
        subgenreById = this.subgenreById,
        metadata = this.getMetadata().toDomain(),
        rawDescription = this.longDesc ?: this.shortDesc ?: "",
        shortDescription = this.shortDesc ?: ""
    )
}

fun MostWatchedContentDto.toEvent(): Event {
    return Event(
        id = this.id,
        title = this.title ?: "",
        description = this.longDesc ?: this.shortDesc ?: "",
        fcIni = this.fcIni,
        fcEnd = this.fcEnd,
        duration = this.duration,
        deliveryURL = this.deliveryURL,
        logoURL = this.logoURL,
        posterLogo = this.posterLogo,
        channelById = this.channelId?.toIntOrNull(),
        subgenreById = this.subgenreById,
        metadata = this.getMetadata().toDomain()
    )
}

fun MostWatchedContentDto.toDomain(): Any {
    return if (this.type == "vod") {
        this.toVoD()
    } else {
        this.toEvent()
    }
}

fun GetRecommendedResponseDto.toDomain(): RecommendedContent {
    return RecommendedContent(
        vods = this.vods?.map { it.toVoD() },
        cutvs = this.cutvs?.map { it.toEvent() }
    )
}
