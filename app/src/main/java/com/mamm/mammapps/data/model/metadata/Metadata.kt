package com.mamm.mammapps.data.model.metadata

import android.os.Parcelable
import com.mamm.mammapps.data.model.section.TbContentItem
import com.mamm.mammapps.data.model.section.TbEventItem
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
            // Agrupa los items por su itemDs, pero mantiene todos los valores para claves duplicadas.
            val groupedItems = items.groupBy({ it.itemDs }, { it.itemValue })

            // Une todos los valores de "cast" en un solo String, separados por "|".
            val allCastValues = groupedItems["cast"]?.joinToString(separator = ",")

            return Metadata(
                actors = allCastValues?.let { Actor.fromItemValue(it) } ?: emptyList(),
                director = groupedItems["director"]?.firstOrNull()?.split("|")?.firstOrNull() ?: "",
                year = groupedItems["year"]?.firstOrNull() ?: "",
                country = groupedItems["country"]?.firstOrNull() ?: "",
                durationMin = groupedItems["duration"]?.firstOrNull() ?: "",
                ratingURL = groupedItems["rating_icon"]?.firstOrNull(),
                genres = groupedItems["genres"]?.firstOrNull() ?: "",
                originalTitle = groupedItems["original_title"]?.firstOrNull() ?: ""
            )
        }

        fun fromTbEventItems(items: List<TbEventItem>): Metadata {
            // Agrupa los items por su itemDs para manejar valores duplicados como "cast".
            val groupedItems = items.groupBy({ it.itemDs }, { it.itemValue })

            // Une todos los valores de "cast" en un solo String, separados por "|".
            val allCastValues = groupedItems["cast"]?.joinToString(separator = ",")

            return Metadata(
                actors = allCastValues?.let { Actor.fromItemValue(it) } ?: emptyList(),
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
