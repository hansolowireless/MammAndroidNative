package com.mamm.mammapps.ui.model

import com.mamm.mammapps.data.model.section.SectionVod

data class SeasonUI(
    val order: Int,
    val title: String,
    val imageUrl: String,
    val episodes: List<ContentEntityUI>
)
