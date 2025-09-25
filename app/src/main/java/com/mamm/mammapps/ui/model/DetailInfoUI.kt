package com.mamm.mammapps.ui.model

import com.mamm.mammapps.data.model.Metadata

data class DetailInfoUI (
    val squareLogo: String? = null,
    val subtitle: String = "",
    val description: String = "",
    val metadata: Metadata? = null,
    val seasons: List<SeasonUI> = emptyList()
)