package com.mamm.mammapps.domain.model.entity

import android.os.Parcelable
import com.mamm.mammapps.domain.model.LogoTransition
import kotlinx.parcelize.Parcelize

@Parcelize
data class Featured(
    val id: Int? = null,
    val title: String? = null,
    val description: String? = null,
    val format: String? = null,
    val logoURL: String? = null,
    val deliveryURL: String? = null,
    val channelById: Int? = null,
    val logoTransitions: List<LogoTransition>? = null,
    val subgenreById: Int? = null,
    val duration: Int? = null
) : Parcelable
