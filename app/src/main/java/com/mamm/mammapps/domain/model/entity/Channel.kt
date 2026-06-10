package com.mamm.mammapps.domain.model.entity

import android.os.Parcelable
import com.mamm.mammapps.domain.model.player.WatermarkInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class Channel(
    val id: Int? = null,
    val name: String? = null,
    val logoURL: String? = null,
    val logoTitleURL: String? = null,
    val description: String? = null,
    val deliveryURL: String? = null,
    val drmUrl: String? = null,
    val timeshift: Boolean? = null,
    val catchupHours: Int? = null,
    val fingerprint: Boolean? = null,
    val fingerprintFrequency: Int? = null,
    val fingerprintDuration: Int? = null,
    val fingerprintPosition: String? = null,
    val fingerPrintText: String? = null,
    val watermark: WatermarkInfo? = null,
    val isPornChannel: Boolean? = false,
    val channelGenre: String? = null,
    val position: Int = Int.MAX_VALUE
): Parcelable
