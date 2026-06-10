package com.mamm.mammapps.data.model.player

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WatermarkInfoDto(
    @SerializedName("has")
    val hasInt: Int = 0,
    @SerializedName("url")
    val url: String? = null
) : Parcelable {
    val has: Boolean get() = hasInt == 1
}
