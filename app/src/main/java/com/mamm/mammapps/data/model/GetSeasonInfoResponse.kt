package com.mamm.mammapps.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.serie.TbSeason
import com.mamm.mammapps.data.model.serie.TbSerieLanguage
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetSeasonInfoResponse(
    @SerializedName("featured")
    val featured: String? = null,

    @SerializedName("in_active")
    val inActive: String? = null,

    @SerializedName("id_serie")
    val idSerie: String? = null,

    @SerializedName("serie_logo_title_url")
    val serieLogoTitleUrl: String? = null,

    @SerializedName("tbSeasons")
    val tbSeasons: List<TbSeason>? = null,

    @SerializedName("id_operator")
    val idOperator: String? = null,

    @SerializedName("serie_logo_url")
    val serieLogoUrl: String? = null,

    @SerializedName("id_parental")
    val idParental: String? = null,

    @SerializedName("tbSerieLanguages")
    val tbSerieLanguages: List<TbSerieLanguage>? = null,

    @SerializedName("poster_logo")
    val posterLogo: String? = null,

    @SerializedName("originalId")
    val originalId: String? = null,

    @SerializedName("id_subgenre")
    val idSubgenre: String? = null
) : Parcelable


@Parcelize
data class TbContentLanguage(
    @SerializedName("short_description")
    val shortDescription: String? = null,

    @SerializedName("id_content")
    val idContent: String? = null,

    @SerializedName("id_language")
    val idLanguage: String? = null,

    @SerializedName("id_content_language")
    val idContentLanguage: String? = null,

    @SerializedName("long_description")
    val longDescription: String? = null,

    @SerializedName("title")
    val title: String? = null
) : Parcelable

