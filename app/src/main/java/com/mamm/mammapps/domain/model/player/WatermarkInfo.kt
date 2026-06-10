package com.mamm.mammapps.domain.model.player

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WatermarkInfo(
    val has: Boolean,
    val url: String?
) : Parcelable
