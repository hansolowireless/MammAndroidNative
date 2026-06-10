package com.mamm.mammapps.domain.model.metadata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Metadata(
    val actors: List<Actor>,
    val director: String,
    val year: String,
    val country: String,
    val durationMin: String,
    val ratingURL: String?,
    val genres: String,
    val originalTitle: String
) : Parcelable
