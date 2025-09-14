package com.mamm.mammapps.data.model.player

data class ContentUpdateData(
    val videoURL: String? = null,
    val licenseURL: String? = null,
    val eventBeginEnd: String? = null,
    val eventChannelName: String? = null,
    val eventTitle: String? = null,
    val isTimeshift: Boolean? = null
)