package com.mamm.mammapps.ui.model

import com.mamm.mammapps.ui.model.player.LiveEventInfoUI
import java.time.ZonedDateTime

data class ContentListUI(
    val identifier: ContentIdentifier,
    val imageUrl: String,
    val title: String,
    val detailInfo: DetailInfoUI? = null,
    val liveEventInfo: LiveEventInfoUI? = null
) {

    val isLive: Boolean
        get() {
            val now = ZonedDateTime.now()

            if (this.liveEventInfo?.eventStart == null || this.liveEventInfo.eventEnd == null) return false

            // Usamos !isBefore y !isAfter para incluir el segundo exacto de inicio y fin
            return !now.isBefore(this.liveEventInfo.eventStart) && !now.isAfter(this.liveEventInfo.eventEnd)
        }
}