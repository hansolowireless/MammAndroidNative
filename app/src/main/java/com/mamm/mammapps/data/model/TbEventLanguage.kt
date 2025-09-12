package com.mamm.mammapps.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class TbEventLanguage(
    @SerializedName("subtitle")
    val subtitle: String? = null,

    @SerializedName("id_language")
    val idLanguage: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("id_event")
    val idEvent: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("id_event_lang")
    val idEventLang: String? = null
) : Parcelable