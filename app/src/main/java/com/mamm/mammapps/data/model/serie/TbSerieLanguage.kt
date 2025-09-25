package com.mamm.mammapps.data.model.serie

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TbSerieLanguage(
    @SerializedName("id_serie")
    val idSerie: String? = null,

    @SerializedName("subtitle")
    val subtitle: String? = null,

    @SerializedName("id_language")
    val idLanguage: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("id_serie_lang")
    val idSerieLang: String? = null,

    @SerializedName("title")
    val title: String? = null
) : Parcelable