package com.mamm.mammapps.data.model.player

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WatermarkInfo(
    @SerializedName("has") val hasInt: Int = 0,
    val url: String? = null
) : Parcelable {
    val has: Boolean get() = hasInt == 1
}
