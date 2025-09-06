package com.mamm.mammapps.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Actor(
    val name: String,
    val image: String
) : Parcelable {
    companion object {
        fun fromItemValue(value: String): List<Actor> {
            return value.split(",").mapNotNull { actorValue ->
                val trimmed = actorValue.trim()
                if (trimmed.isBlank()) null else parseActorFromValue(trimmed)
            }
        }

        private fun parseActorFromValue(value: String): Actor {
            val parts = value.split("|", limit = 2)
            return Actor(
                name = parts[0],
                image = parts.getOrNull(1) ?: ""
            )
        }
    }
}
