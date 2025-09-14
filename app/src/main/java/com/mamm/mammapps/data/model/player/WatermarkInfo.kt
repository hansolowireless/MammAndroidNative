package com.mamm.mammapps.data.model.player

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WatermarkInfo(
    val has: Boolean = false,
    val url: String? = null
) : Parcelable
