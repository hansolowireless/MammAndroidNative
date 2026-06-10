package com.mamm.mammapps.data.mapper

import com.mamm.mammapps.data.model.serie.EpisodeDto
import com.mamm.mammapps.data.model.serie.GetSeasonInfoResponseDto
import com.mamm.mammapps.data.model.serie.TbContentSeasonDto
import com.mamm.mammapps.data.model.serie.TbSeasonDto
import com.mamm.mammapps.domain.model.entity.VoD
import com.mamm.mammapps.domain.model.serie.SerieInfo
import com.mamm.mammapps.domain.model.serie.TbContentSeason
import com.mamm.mammapps.domain.model.serie.Season

fun EpisodeDto.toDomain(): VoD {
    return VoD(
        contentLogo = this.contentLogo,
        posterURL = this.posterLogo,
        duration = this.duration?.toIntOrNull(),
        deliveryURL = this.path,
        subgenreById = this.idSubgenre?.toIntOrNull(),
        
        // Mapped values
        title = this.getTitle(),
        rawDescription = this.getDescription(),
        shortDescription = this.getShortDescription(),
        
        // Base VoD properties
        id = this.idContent?.toIntOrNull()
    )
}

fun TbContentSeasonDto.toDomain(): TbContentSeason {
    return TbContentSeason(
        chapter = this.chapter,
        idContent = this.idContent,
        idSeason = this.idSeason,
        contentDetails = this.contentDetails?.toDomain(),
        idContentSeason = this.idContentSeason
    )
}

fun TbSeasonDto.toDomain(): Season {
    return Season(
        featured = this.featured,
        inActive = this.inActive,
        idSerie = this.idSerie,
        idOperator = this.idOperator,
        seasonLogoTitleUrl = this.seasonLogoTitleUrl,
        idSeason = this.idSeason,
        seasonLogoUrl = this.seasonLogoUrl,
        originalId = this.originalId,
        tbContentSeasons = this.tbContentSeasons?.map { it.toDomain() },
        order = this.order
    )
}

fun GetSeasonInfoResponseDto.toDomain(): SerieInfo {
    return SerieInfo(
        featured = this.featured,
        inActive = this.inActive,
        idSerie = this.idSerie,
        serieLogoTitleUrl = this.serieLogoTitleUrl,
        tbSeasons = this.tbSeasons?.map { it.toDomain() },
        idOperator = this.idOperator,
        serieLogoUrl = this.serieLogoUrl,
        idParental = this.idParental,
        posterLogo = this.posterLogo,
        originalId = this.originalId,
        idSubgenre = this.idSubgenre
    )
}
