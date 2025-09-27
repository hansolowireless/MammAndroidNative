package com.mamm.mammapps.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ContentIdentifier : Parcelable {
    abstract val id: Int

    data class Channel(override val id: Int) : ContentIdentifier()
    data class VoD(override val id: Int) : ContentIdentifier()
    data class Event(override val id: Int) : ContentIdentifier()
    data class Serie(override val id: Int) : ContentIdentifier()

    companion object {
        fun fromFormat(format: String, id: Int): ContentIdentifier = when (format.lowercase()) {
            "channel" -> Channel(id)
            "vod" -> VoD(id)
            "cutv" -> Event(id)
            "serie" -> Serie(id)
            else -> throw IllegalArgumentException("Unknown type: $format")
        }
    }

    fun getIdValue(): Int {
        return when (this) {
            is Channel -> this.id
            is VoD -> this.id
            is Event -> this.id
            is Serie -> this.id
        }
    }

}
