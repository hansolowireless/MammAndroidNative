package com.mamm.mammapps.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LogoTransition(
    val url: String?
) : Parcelable
