package com.mamm.mammapps.data.mapper

import com.mamm.mammapps.data.model.player.QosDataDto
import com.mamm.mammapps.domain.model.player.QosData

fun QosDataDto.toDomain(): QosData {
    return QosData(
        deviceType = this.deviceType,
        playerBw = this.playerBw,
        activeTrack = this.activeTrack,
        videoBw = this.videoBw,
        bufTime = this.bufTime,
        loadLatency = this.loadLatency,
        playTime = this.playTime,
        primaryNode = this.primaryNode,
        id = this.id,
        type = this.type,
        ip = this.ip,
        adId = this.adId
    )
}

fun QosData.toDto(): QosDataDto {
    return QosDataDto(
        deviceType = this.deviceType,
        playerBw = this.playerBw,
        activeTrack = this.activeTrack,
        videoBw = this.videoBw,
        bufTime = this.bufTime,
        loadLatency = this.loadLatency,
        playTime = this.playTime,
        primaryNode = this.primaryNode,
        id = this.id,
        type = this.type,
        ip = this.ip,
        adId = this.adId
    )
}
