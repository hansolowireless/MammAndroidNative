package com.mamm.mammapps.ui.model

import androidx.compose.ui.unit.Dp
import com.mamm.mammapps.ui.model.player.LiveEventInfoUI
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.Ratios

data class ContentEntityUI(
    val identifier: ContentIdentifier,
    val imageUrl: String,
    val title: String,
    val detailInfo: DetailInfoUI? = null,
    var liveEventInfo: LiveEventInfoUI? = null,
    val aspectRatio: Float = Ratios.HORIZONTAL,
    val height: Dp = Dimensions.channelEntityHeight,
)