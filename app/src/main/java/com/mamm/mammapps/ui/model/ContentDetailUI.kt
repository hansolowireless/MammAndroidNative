package com.mamm.mammapps.ui.model

import androidx.compose.ui.unit.Dp
import com.mamm.mammapps.data.model.Metadata
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.Ratios

data class ContentDetailUI (
    val identifier: ContentIdentifier,
    val imageUrl: String,
    val title: String,
    val subtitle: String = "",
    val description: String? = null,
    val metadata: Metadata? = null
)