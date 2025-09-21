package com.mamm.mammapps.ui.model.player

import com.mamm.mammapps.data.model.player.WatermarkInfo
import com.mamm.mammapps.ui.model.ContentIdentifier

data class ContentToPlayUI (
    val identifier: ContentIdentifier,
    val imageUrl: String,
    val title: String,
    val subtitle: String? = null,
    val description: String? = null,
    val deliveryURL: String,
    val isTimeshift: Boolean = false,
    val isRadio: Boolean = false,
    val initialPlayPositionMs: Long = 0,
    val epgEventInfo: LiveEventInfoUI? = null,
    val fingerprintInfo: FingerPrintInfoUI? = null,
    val watermarkInfo: WatermarkInfo? = null
) {
    val isLive: Boolean = this.identifier is ContentIdentifier.Channel

    fun getDRMString () : String {
        return when (identifier) {
            is ContentIdentifier.Channel -> "LIVE"
            is ContentIdentifier.VoD -> "VOD"
            is ContentIdentifier.Event -> "VOD"
            is ContentIdentifier.Serie -> "SERIE"
        }
    }

    fun getCLMString() : String {
        return when (identifier) {
            is ContentIdentifier.Channel -> "LIVE"
            is ContentIdentifier.VoD -> "CUTV"
            is ContentIdentifier.Event -> "VOD"
            is ContentIdentifier.Serie -> "SERIE"
        }
    }

}