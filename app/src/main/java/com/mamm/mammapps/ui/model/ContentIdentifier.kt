package com.mamm.mammapps.ui.model

import android.os.Parcelable
import com.mamm.mammapps.domain.model.entity.FeaturedFormat
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

        fun fromFeaturedFormat(type: FeaturedFormat, id: String, channelById: Int? = 0): ContentIdentifier {
            val numericId = id.toIntOrNull() ?: id.hashCode().let { if (it < 0) -it else it }
            return when (type) {
                FeaturedFormat.LIVE -> Channel(channelById ?: 0)
                FeaturedFormat.VOD -> VoD(numericId)
                FeaturedFormat.CUTV -> Event(numericId)
                FeaturedFormat.STILL -> Serie(numericId)
                FeaturedFormat.BANNER -> Serie(numericId)
                FeaturedFormat.UNKNOWN -> VoD(numericId)
            }
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

    fun getQoSString () : String {
        return when (this) {
            is Channel -> "live"
            is VoD -> "vod"
            is Event -> "cutv"
            is Serie -> "serie"
        }
    }

}
