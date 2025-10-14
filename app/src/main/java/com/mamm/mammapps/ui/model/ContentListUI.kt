package com.mamm.mammapps.ui.model

data class ContentListUI(
    val identifier: ContentIdentifier,
    val imageUrl: String,
    val title: String,
    val detailInfo: DetailInfoUI? = null,
)