package com.mamm.mammapps.data.model.serie

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TbSeason(
    @SerializedName("featured")
    val featured: String? = null,

    @SerializedName("in_active")
    val inActive: String? = null,

    @SerializedName("id_serie")
    val idSerie: String? = null,

    @SerializedName("id_operator")
    val idOperator: String? = null,

    @SerializedName("season_logo_title_url")
    val seasonLogoTitleUrl: String? = null,

    @SerializedName("id_season")
    val idSeason: String? = null,

    @SerializedName("season_logo_url")
    val seasonLogoUrl: String? = null,

    @SerializedName("tbSeasonLanguages")
    val tbSeasonLanguages: List<TbSeasonLanguage>? = null,

    @SerializedName("originalId")
    val originalId: String? = null,

    @SerializedName("tbContentSeasons")
    val tbContentSeasons: List<TbContentSeason>? = null,

    @SerializedName("order")
    val order: String? = null
) : Parcelable {

    fun getOrder(): Int {
        return order?.toIntOrNull() ?: 0
    }

    fun getTitle(): String {
        return tbSeasonLanguages?.firstOrNull()?.title ?: ""
    }

}