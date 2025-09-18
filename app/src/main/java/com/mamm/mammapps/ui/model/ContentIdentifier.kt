package com.mamm.mammapps.ui.model

sealed class ContentIdentifier {
    data class Channel(val id: Int) : ContentIdentifier()
    data class VoD(val id: Int) : ContentIdentifier()
    data class Event(val id: Int) : ContentIdentifier()
    data class Serie(val id: Int) : ContentIdentifier()

    fun getIdValue(): Int {
        return when (this) {
            is Channel -> this.id
            is VoD -> this.id
            is Event -> this.id
            is Serie -> this.id
        }
    }
}
