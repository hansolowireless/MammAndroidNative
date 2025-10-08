package com.mamm.mammapps.ui.model

data class ContentRowUI(
    val categoryName: String,
    val items: List<ContentEntityUI>,
    val isFeatured: Boolean = false
)