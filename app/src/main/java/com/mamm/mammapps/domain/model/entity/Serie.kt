package com.mamm.mammapps.domain.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Serie(
    val subgenreById: Int? = null,
    val featured: Int? = null,
    val serieLogoUrl: String? = null,
    val active: Int? = null,
    val shortDesc: String? = null,
    val logoTitleURL: String? = null,
    val id: Int? = null,
    val title: String? = null,
    val longDesc: String? = null
) : Parcelable
