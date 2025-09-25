package com.mamm.mammapps.ui.model

data class SeasonUI(
    val order: Int,
    val title: String,
    val imageUrl: String,
    val episodes: List<ContentListUI>
)
