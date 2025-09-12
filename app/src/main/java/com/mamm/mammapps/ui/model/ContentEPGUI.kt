package com.mamm.mammapps.ui.model

data class ContentEPGUI(
    val identifier: ContentIdentifier,
    val imageUrl: String,
    val title: String,
) {
    val id: String get() = when (identifier) {
        is ContentIdentifier.Channel -> identifier.id
        is ContentIdentifier.VoD -> identifier.id
        is ContentIdentifier.Event -> identifier.id
        is ContentIdentifier.Serie -> identifier.id
    }
}