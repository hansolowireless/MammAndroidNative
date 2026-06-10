package com.mamm.mammapps.data.model.metadata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActorDto(
    val name: String,
    val image: String
) : Parcelable {
    companion object {
        fun fromItemValue(value: String): List<ActorDto> {
            return value.split(",").mapNotNull { actorValue ->
                val trimmed = actorValue.trim()
                if (trimmed.isBlank()) null else parseActorFromValue(trimmed)
            }
        }

        private fun parseActorFromValue(value: String): ActorDto {
            val parts = value.split("|", limit = 2)
            return ActorDto(
                name = parts[0],
                image = parts.getOrNull(1) ?: ""
            )
        }
    }
}
