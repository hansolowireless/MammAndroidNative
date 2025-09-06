package com.mamm.mammapps.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Metadata(
    val actors: List<Actor>,
    val director: String,
    val year: String,
    val country: String,
    val durationMin: String,
    val ratingURL: String?,
    val genres: String,
    val originalTitle: String
) : Parcelable {
    companion object {
        fun fromTbContentItems(items: List<TbContentItem>): Metadata {
            val itemMap = items.associate { it.itemDs to it.itemValue }

            return Metadata(
                actors = itemMap["cast"]?.let { Actor.fromItemValue(it) } ?: emptyList(),
                director = itemMap["director"]?.split("|")?.firstOrNull() ?: "",
                year = itemMap["year"] ?: "",
                country = itemMap["country"] ?: "",
                durationMin = itemMap["duration"] ?: "",
                ratingURL = itemMap["rating_icon"],
                genres = itemMap["genres"] ?: "",
                originalTitle = itemMap["original_title"] ?: ""
            )
        }
    }
}
