package com.mamm.mammapps.data.mapper.topchannels

import com.mamm.mammapps.data.model.topchannels.TopChannelDto
import com.mamm.mammapps.data.model.topchannels.TopChannelsResponseDto
import com.mamm.mammapps.domain.model.topchannels.TopChannel
import com.mamm.mammapps.domain.model.topchannels.TopChannels

fun TopChannelsResponseDto.toDomain(): TopChannels {
    return TopChannels(
        email = this.email,
        updatedAt = this.updatedAt,
        channels = this.channels.map { it.toDomain() }
    )
}

fun TopChannelDto.toDomain(): TopChannel {
    return TopChannel(
        channelId = this.channelId,
        hits2d = this.hits2d,
        hits3to7d = this.hits3to7d,
        score = this.score
    )
}
