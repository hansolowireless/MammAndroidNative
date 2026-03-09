package com.mamm.mammapps.data.model.topchannels

import com.google.gson.annotations.SerializedName

data class TopChannelsResponseDto(
    @SerializedName("email") val email: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("channels") val channels: List<TopChannelDto>
)

data class TopChannelDto(
    @SerializedName("channel_id") val channelId: String,
    @SerializedName("hits_2d") val hits2d: Int,
    @SerializedName("hits_3to7d") val hits3to7d: Int,
    @SerializedName("score") val score: Double
)
