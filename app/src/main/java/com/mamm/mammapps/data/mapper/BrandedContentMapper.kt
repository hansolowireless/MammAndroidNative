package com.mamm.mammapps.data.mapper

import com.mamm.mammapps.data.model.GetBrandedContentResponseDto
import com.mamm.mammapps.data.model.GetOtherContentResponseDto
import com.mamm.mammapps.data.model.branded.BrandedFeaturedDto
import com.mamm.mammapps.data.model.branded.BrandedVodDto
import com.mamm.mammapps.data.model.section.SectionVodDto
import com.mamm.mammapps.domain.model.BrandedContent
import com.mamm.mammapps.domain.model.LogoTransition
import com.mamm.mammapps.domain.model.OtherContent
import com.mamm.mammapps.domain.model.entity.Featured
import com.mamm.mammapps.domain.model.entity.FeaturedFormat
import com.mamm.mammapps.domain.model.entity.VoD
import com.mamm.mammapps.data.model.branded.LogoTransitionDto as BrandedLogoTransitionDto
import com.mamm.mammapps.data.model.section.LogoTransitionDto as SectionLogoTransitionDto

fun BrandedLogoTransitionDto.toDomain(): LogoTransition {
    return LogoTransition(url = this.url)
}

fun SectionLogoTransitionDto.toDomain(): LogoTransition {
    return LogoTransition(url = this.url)
}

fun BrandedFeaturedDto.toDomain(): Featured {
    return Featured(
        id = this.formatId ?: this.id?.toString(),
        title = this.title,
        description = this.description,
        type = FeaturedFormat.from(this.format),
        logoURL = this.logoUrl,
        deliveryURL = this.deliveryUrl,
        channelById = this.channelById,
        logoTransitions = this.logoTransitions?.map { it.toDomain() },
        subgenreById = this.subgenreById?.toIntOrNull(),
        duration = this.duration?.toIntOrNull()
    )
}

fun BrandedVodDto.toDomain(): VoD {
    return VoD(
        contentLogo = this.contentLogo,
        posterURL = this.posterLogo,
        logoURL = this.logoUrl,
        duration = this.duration?.toIntOrNull(),
        deliveryURL = this.path,
        subgenreById = this.idSubgenre?.toIntOrNull(),
        
        // Mapped values
        title = this.getTitle(),
        rawDescription = this.getDescription(),
        metadata = this.getMetadata().toDomain(),
        
        // Populate base VoD properties for uniformity
        id = this.idContent?.toIntOrNull()
    )
}

fun SectionVodDto.toDomain(): VoD {
    return VoD(
        id = this.id,
        title = this.title.orEmpty(),
        shortDesc = this.shortDesc.orEmpty(),
        longDesc = this.longDesc.orEmpty(),
        duration = this.duration,
        deliveryURL = this.deliveryURL,
        logoURL = this.logoURL,
        posterURL = this.posterURL,
        contentLogo = this.contentLogo,
        subgenreById = this.subgenreById,
        metadata = this.getMetadata().toDomain(),
        
        // Populate explicitly mapped fields too
        rawDescription = this.getDescription()
    )
}

fun GetBrandedContentResponseDto.toDomain(): BrandedContent {
    return BrandedContent(
        featured = this.featured?.map { it.toDomain() },
        channels = this.channels?.map { it.toDomain() },
        vods = this.vods?.map { it.toDomain() },
        events = this.events?.map { it.toDomain() },
        series = this.series?.map { it.toDomain() }
    )
}

fun GetOtherContentResponseDto.toDomain(): OtherContent {
    return OtherContent(
        events = this.events?.map { it.toDomain() },
        vods = this.vods?.map { it.toDomain() }
    )
}
