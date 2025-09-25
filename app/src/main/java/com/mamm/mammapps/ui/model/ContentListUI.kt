package com.mamm.mammapps.ui.model

import com.mamm.mammapps.ui.model.player.LiveEventInfoUI

data class ContentListUI(
    val identifier: ContentIdentifier,
    val imageUrl: String,
    val title: String,
    val detailInfo: DetailInfoUI? = null,
)