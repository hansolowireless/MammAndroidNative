package com.mamm.mammapps.data.model.player.streamvx

import com.google.gson.annotations.SerializedName

data class StreamVxTokenRequest(
    @SerializedName("requestVerification")
    val requestVerification: String,
    @SerializedName("signature")
    val signature: String
)
