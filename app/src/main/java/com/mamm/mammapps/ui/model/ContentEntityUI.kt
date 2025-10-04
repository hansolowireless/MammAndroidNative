package com.mamm.mammapps.ui.model

import android.os.Parcelable
import androidx.compose.ui.unit.Dp
import com.mamm.mammapps.ui.model.player.LiveEventInfoUI
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.Ratios
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ContentEntityUI(
    val identifier: ContentIdentifier,
    val imageUrl: String,
    val horizontalImageUrl: String = "",
    val title: String,
    val detailInfo: DetailInfoUI? = null,
    var liveEventInfo: LiveEventInfoUI? = null,
    val aspectRatio: Float = Ratios.HORIZONTAL,
    val height: @RawValue Dp = Dimensions.channelEntityHeight,
    val customContentType: CustomizedContent = CustomizedContent.None
) : Parcelable