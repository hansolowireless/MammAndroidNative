package com.mamm.mammapps.ui.model

import androidx.compose.ui.unit.Dp
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.util.AppConstants.Companion.HORIZONTAL_ASPECT_RATIO

data class ContentEntityUI(
    val imageUrl: String,
    val title: String,
    val subtitle: String? = null,
    val aspectRatio: Float = HORIZONTAL_ASPECT_RATIO,
    val height: Dp = Dimensions.channelEntityHeight
)