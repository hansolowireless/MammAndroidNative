package com.mamm.mammapps.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TbContentItem(
    @SerializedName("id_content") val idContent: Int? = null,
    @SerializedName("item_ds") val itemDs: String? = null,
    @SerializedName("id_content_item") val idContentItem: Int? = null,
    @SerializedName("item_value") val itemValue: String? = null
): Parcelable