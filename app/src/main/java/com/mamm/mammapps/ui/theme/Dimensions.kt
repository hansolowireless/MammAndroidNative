package com.mamm.mammapps.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Dimensions {
    val paddingXSmall = 8.dp
    val paddingSmall = 12.dp
    val paddingMedium = 16.dp
    val paddingMediumLarge = 20.dp
    val paddingLarge = 24.dp
    val paddingXLarge = 30.dp
    val posterRowHeight = 190.dp
    val landscapeRowHeight = 140.dp
    val cornerRadius = 10.dp
    val buttonBorder = 2.dp
    val tvNavRailWidth = 60.dp
    val tvNavRailWidthExpanded = 200.dp

    // Altura de las tarjetas en las filas horizontales; el ancho sale de la proporción
    fun rowCardHeight(aspectRatio: Float): Dp =
        if (aspectRatio < 1f) posterRowHeight else landscapeRowHeight
}

object Ratios{
    const val HORIZONTAL = 16f / 9f
    const val VERTICAL = 10f / 16f
}