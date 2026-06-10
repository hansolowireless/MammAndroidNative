package com.mamm.mammapps.data.mapper

import com.mamm.mammapps.data.model.metadata.ActorDto
import com.mamm.mammapps.data.model.metadata.MetadataDto
import com.mamm.mammapps.domain.model.metadata.Actor
import com.mamm.mammapps.domain.model.metadata.Metadata

fun MetadataDto.toDomain(): Metadata {
    return Metadata(
        actors = this.actors.map { it.toDomain() },
        director = this.director,
        year = this.year,
        country = this.country,
        durationMin = this.durationMin,
        ratingURL = this.ratingURL,
        genres = this.genres,
        originalTitle = this.originalTitle
    )
}

fun ActorDto.toDomain(): Actor {
    return Actor(
        name = this.name,
        image = this.image
    )
}
