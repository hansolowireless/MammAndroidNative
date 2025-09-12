package com.mamm.mammapps.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TbEventItem(
    @SerializedName("id_event") val idEvent: Int? = null,
    @SerializedName("item_ds") val itemDs: String? = null,
    @SerializedName("id_event_item") val idEventItem: Int? = null,
    @SerializedName("item_value") val itemValue: String? = null
): Parcelable