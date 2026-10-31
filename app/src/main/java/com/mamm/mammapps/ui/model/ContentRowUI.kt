package com.mamm.mammapps.ui.model

import com.mamm.mammapps.ui.theme.Ratios

data class ContentRowUI(
    val categoryId: Int = 0,
    val categoryName: String,
    val items: List<ContentEntityUI>,
    val isFeatured: Boolean = false,
    val loadMore: Boolean = false,
) {
    // Proporción mayoritaria de la fila
    val expandCategoryAspectRatio: Float = items.groupingBy { it.aspectRatio }.eachCount()
        .maxByOrNull { it.value }?.key ?: Ratios.HORIZONTAL
}