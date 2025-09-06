package com.mamm.mammapps.ui.model

import androidx.compose.ui.unit.Dp
import com.mamm.mammapps.data.model.Metadata
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.util.AppConstants.Companion.HORIZONTAL_ASPECT_RATIO

data class ContentDetailUI (
    val identifier: ContentIdentifier,
    val imageUrl: String,
    val title: String,
    val description: String? = null,
    val metadata: Metadata? = null,
    val aspectRatio: Float = HORIZONTAL_ASPECT_RATIO,
    val height: Dp = Dimensions.channelEntityHeight
)