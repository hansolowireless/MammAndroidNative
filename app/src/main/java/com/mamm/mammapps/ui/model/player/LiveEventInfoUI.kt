package com.mamm.mammapps.ui.model.player

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class LiveEventInfoUI (
    val title: String,
    val logoURL: String = "",
    val deliveryURL: String = "",
    val eventStart: ZonedDateTime?,
    val eventEnd: ZonedDateTime?
) : Parcelable