package com.mamm.mammapps.domain.model.serie

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Season(
    val featured: String? = null,
    val inActive: String? = null,
    val idSerie: String? = null,
    val idOperator: String? = null,
    val seasonLogoTitleUrl: String? = null,
    val idSeason: String? = null,
    val seasonLogoUrl: String? = null,
    val originalId: String? = null,
    val tbContentSeasons: List<TbContentSeason>? = null,
    val order: String? = null
) : Parcelable {

    fun getOrder(): Int {
        return order?.toIntOrNull() ?: 0
    }

    fun getTitle(): String {
        return "T " + getOrder()
    }

}
