package com.mamm.mammapps.data.model.metadata

import android.os.Parcelable
import com.mamm.mammapps.data.model.section.TbContentItemDto
import com.mamm.mammapps.data.model.section.TbEventItemDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class MetadataDto(
    val actors: List<ActorDto>,
    val director: String,
    val year: String,
    val country: String,
    val durationMin: String,
    val ratingURL: String?,
    val genres: String,
    val originalTitle: String
) : Parcelable {
    companion object {
        fun fromTbContentItems(items: List<TbContentItemDto>): MetadataDto {
            val groupedItems = items.groupBy({ it.itemDs }, { it.itemValue })
            val allCastValues = groupedItems["cast"]?.joinToString(separator = ",")

            return MetadataDto(
                actors = allCastValues?.let { ActorDto.fromItemValue(it) } ?: emptyList(),
                director = groupedItems["director"]?.firstOrNull()?.split("|")?.firstOrNull() ?: "",
                year = groupedItems["year"]?.firstOrNull() ?: "",
                country = groupedItems["country"]?.firstOrNull() ?: "",
                durationMin = groupedItems["duration"]?.firstOrNull() ?: "",
                ratingURL = groupedItems["rating_icon"]?.firstOrNull(),
                genres = groupedItems["genres"]?.firstOrNull() ?: "",
                originalTitle = groupedItems["original_title"]?.firstOrNull() ?: ""
            )
        }

        fun fromTbEventItems(items: List<TbEventItemDto>): MetadataDto {
            val groupedItems = items.groupBy({ it.itemDs }, { it.itemValue })
            val allCastValues = groupedItems["cast"]?.joinToString(separator = ",")

            return MetadataDto(
                actors = allCastValues?.let { ActorDto.fromItemValue(it) } ?: emptyList(),
                director = groupedItems["director"]?.firstOrNull()?.split("|")?.firstOrNull() ?: "",
                year = groupedItems["year"]?.firstOrNull() ?: "",
                country = groupedItems["country"]?.firstOrNull() ?: "",
                durationMin = groupedItems["duration"]?.firstOrNull() ?: "",
                ratingURL = groupedItems["rating_icon"]?.firstOrNull(),
                genres = groupedItems["genres"]?.firstOrNull() ?: "",
                originalTitle = groupedItems["original_title"]?.firstOrNull() ?: ""
            )
        }
    }
}
