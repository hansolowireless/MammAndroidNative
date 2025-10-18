package com.mamm.mammapps.ui.model

data class ContentRowUI(
    val categoryId: Int = 0,
    val categoryName: String,
    val items: List<ContentEntityUI>,
    val isFeatured: Boolean = false,
    val loadMore: Boolean = false,
)