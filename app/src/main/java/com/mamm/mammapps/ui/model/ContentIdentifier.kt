package com.mamm.mammapps.ui.model

sealed class ContentIdentifier {
    abstract val id: Int

    data class Channel(override val id: Int) : ContentIdentifier()
    data class VoD(override val id: Int) : ContentIdentifier()
    data class Event(override val id: Int) : ContentIdentifier()
    data class Serie(override val id: Int) : ContentIdentifier()

    fun getIdValue(): Int {
        return when (this) {
            is Channel -> this.id
            is VoD -> this.id
            is Event -> this.id
            is Serie -> this.id
        }
    }
}
