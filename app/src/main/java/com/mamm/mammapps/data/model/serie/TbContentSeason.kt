package com.mamm.mammapps.data.model.serie

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TbContentSeason(
    @SerializedName("chapter")
    val chapter: String? = null,

    @SerializedName("id_content")
    val idContent: String? = null,

    @SerializedName("id_season")
    val idSeason: String? = null,

    @SerializedName("idContent")
    val contentDetails: Episode? = null,

    @SerializedName("id_content_season")
    val idContentSeason: String? = null
) : Parcelable