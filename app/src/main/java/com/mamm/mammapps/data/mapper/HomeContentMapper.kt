package com.mamm.mammapps.data.mapper

import com.mamm.mammapps.data.model.CategoryDto
import com.mamm.mammapps.data.model.ChannelDto
import com.mamm.mammapps.data.model.EventDto
import com.mamm.mammapps.data.model.GenreDto
import com.mamm.mammapps.data.model.GetHomeContentResponseDto
import com.mamm.mammapps.data.model.HomeFeaturedDto
import com.mamm.mammapps.data.model.LogoTransitionDto
import com.mamm.mammapps.data.model.OrderItemDto
import com.mamm.mammapps.data.model.SerieDto
import com.mamm.mammapps.data.model.SubgenreDto
import com.mamm.mammapps.data.model.VoDDto
import com.mamm.mammapps.data.model.player.WatermarkInfoDto
import com.mamm.mammapps.domain.model.Category
import com.mamm.mammapps.domain.model.entity.Channel
import com.mamm.mammapps.domain.model.entity.Event
import com.mamm.mammapps.domain.model.Genre
import com.mamm.mammapps.domain.model.HomeContent
import com.mamm.mammapps.domain.model.entity.Featured
import com.mamm.mammapps.domain.model.LogoTransition
import com.mamm.mammapps.domain.model.OrderItem
import com.mamm.mammapps.domain.model.entity.Serie
import com.mamm.mammapps.domain.model.Subgenre
import com.mamm.mammapps.domain.model.entity.VoD
import com.mamm.mammapps.domain.model.player.WatermarkInfo

fun WatermarkInfoDto.toDomain(): WatermarkInfo {
    return WatermarkInfo(
        has = this.has,
        url = this.url
    )
}

fun LogoTransitionDto.toDomain(): LogoTransition {
    return LogoTransition(
        url = this.url
    )
}

fun HomeFeaturedDto.toDomain(): Featured {
    return Featured(
        id = this.id,
        title = this.title,
        description = this.description,
        format = this.format,
        logoURL = this.logoURL,
        deliveryURL = this.deliveryURL,
        channelById = this.channelById,
        logoTransitions = this.logoTransitions?.map { it.toDomain() },
        subgenreById = this.subgenreById,
        duration = this.duration
    )
}

fun ChannelDto.toDomain(): Channel {
    return Channel(
        id = this.id,
        name = this.name,
        logoURL = this.logoURL,
        logoTitleURL = this.logoTitleURL,
        description = this.description,
        deliveryURL = this.deliveryURL,
        drmUrl = this.drmUrl,
        timeshift = this.timeshift,
        catchupHours = this.catchupHours,
        fingerprint = this.fingerprint,
        fingerprintFrequency = this.fingerprintFrequency,
        fingerprintDuration = this.fingerprintDuration,
        fingerprintPosition = this.fingerprintPosition,
        fingerPrintText = this.fingerPrintText,
        watermark = this.watermark?.toDomain(),
        isPornChannel = this.isPornChannel,
        channelGenre = this.channelGenre,
        position = this.position
    )
}

fun VoDDto.toDomain(): VoD {
    return VoD(
        contentLogo = this.contentLogo,
        subgenreById = this.subgenreById,
        title = this.title ?: "",
        logoURL = this.logoURL,
        duration = this.duration,
        posterURL = this.posterURL,
        shortDesc = this.shortDesc ?: "",
        deliveryURL = this.deliveryURL,
        id = this.id,
        longDesc = this.longDesc ?: "",
        metadata = this.metadata?.toDomain()
    )
}

fun GenreDto.toDomain(): Genre {
    return Genre(
        subgenres = this.subgenres?.map { it.toDomain() },
        logo = this.logo,
        id = this.id,
        ds = this.ds
    )
}

fun SubgenreDto.toDomain(): Subgenre {
    return Subgenre(
        descripcion = this.descripcion,
        logo = this.logo,
        id = this.id,
        ds = this.ds
    )
}

fun CategoryDto.toDomain(): Category {
    return Category(
        catchupRow = this.catchupRow,
        pos = this.pos,
        name = this.name,
        id = this.id,
        loadMore = this.loadMore,
        order = this.order?.map { it.toDomain() }
    )
}

fun OrderItemDto.toDomain(): OrderItem {
    return OrderItem(
        pos = this.pos,
        id = this.id,
        type = this.type
    )
}

fun EventDto.toDomain(): Event {
    return Event(
        subgenreById = this.subgenreById,
        logoTransitions = this.logoTransitions?.map { it.toDomain() },
        description = this.description ?: "",
        title = this.title ?: "",
        fcIni = this.fcIni,
        logoURL = this.logoURL,
        duration = this.duration,
        fcEnd = this.fcEnd,
        subtitle = this.subtitle ?: "",
        deliveryURL = this.deliveryURL,
        channelById = this.channelById,
        id = this.id,
        parental = this.parental
    )
}

fun SerieDto.toDomain(): Serie {
    return Serie(
        subgenreById = this.subgenreById,
        featured = this.featured,
        serieLogoUrl = this.serieLogoUrl,
        active = this.active,
        shortDesc = this.shortDesc,
        logoTitleURL = this.logoTitleURL,
        id = this.id,
        title = this.title,
        longDesc = this.longDesc
    )
}

fun GetHomeContentResponseDto.toDomain(): HomeContent {
    return HomeContent(
        timeGenerated = this.timeGenerated,
        featured = this.featured?.map { it.toDomain() },
        channels = this.channels?.map { it.toDomain() },
        contents = this.contents?.map { it.toDomain() },
        genres = this.genres?.map { it.toDomain() },
        categories = this.categories?.map { it.toDomain() },
        events = this.events?.map { it.toDomain() },
        series = this.series?.map { it.toDomain() }
    )
}
