package com.mamm.mammapps.data.model.player.streamvx

import com.google.gson.annotations.SerializedName

data class StreamVxTokenResponse(
    @SerializedName("data")
    val data: StreamVxTokenData?,
    @SerializedName("status")
    val status: Int?
)

data class StreamVxTokenData(
    @SerializedName("token")
    val token: String?
)
