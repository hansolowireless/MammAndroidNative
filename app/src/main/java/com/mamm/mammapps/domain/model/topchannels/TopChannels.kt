package com.mamm.mammapps.domain.model.topchannels

data class TopChannels(
    val email: String,
    val updatedAt: String,
    val channels: List<TopChannel>
)

data class TopChannel(
    val channelId: String,
    val hits2d: Int,
    val hits3to7d: Int,
    val score: Double
)
