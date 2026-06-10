package com.mamm.mammapps.domain.model.serie

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SerieInfo(
    val featured: String? = null,
    val inActive: String? = null,
    val idSerie: String? = null,
    val serieLogoTitleUrl: String? = null,
    val tbSeasons: List<Season>? = null,
    val idOperator: String? = null,
    val serieLogoUrl: String? = null,
    val idParental: String? = null,
    val posterLogo: String? = null,
    val originalId: String? = null,
    val idSubgenre: String? = null
) : Parcelable
