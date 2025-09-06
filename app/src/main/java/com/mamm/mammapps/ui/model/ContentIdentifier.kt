package com.mamm.mammapps.ui.model

sealed class ContentIdentifier {
    data class Channel(val id: String) : ContentIdentifier()
    data class VoD(val id: String) : ContentIdentifier()
    data class Event(val id: String) : ContentIdentifier()
    data class Serie(val id: String) : ContentIdentifier()
}
