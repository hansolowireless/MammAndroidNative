package com.mamm.mammapps.data.mapper

import com.mamm.mammapps.data.model.diagnostic.DiagResponseDto
import com.mamm.mammapps.data.model.player.GetTickersResponseDto
import com.mamm.mammapps.data.model.player.TickerDto
import com.mamm.mammapps.data.model.sportsevent.SportsEventDto
import com.mamm.mammapps.domain.model.DiagnosticInfo
import com.mamm.mammapps.domain.model.SportsEvent
import com.mamm.mammapps.domain.model.player.Ticker
import com.mamm.mammapps.domain.model.player.TickerInfo
import com.mamm.mammapps.util.parseSportEventDate

fun DiagResponseDto.toDomain(): DiagnosticInfo {
    return DiagnosticInfo(
        node1Url = formatUrl(this.nodeHA.node01),
        node2Url = formatUrl(this.nodeHA.node02),
        node3Url = formatUrl(this.nodeDir.node01),
        node4Url = formatUrl(this.nodeDir.node02)
    )
}

fun SportsEventDto.toDomain() : SportsEvent {
    return SportsEvent(
        title = this.title ?: "",
        description = this.desc ?: "",
        startTime = parseSportEventDate(this.start),
        endTime = parseSportEventDate(this.stop),
        channelId = this.channel ?: "",
        horizontalImage = this.icons?.find { it.contains("_B") }.orEmpty(),
        verticalImage = this.icons?.find { it.contains("_P") }.orEmpty()
    )
}

fun GetTickersResponseDto.toDomain() : TickerInfo {
    return TickerInfo(
        fechaGeneracion = this.fechaGeneracion,
        tickers = this.tickers.map {it.toDomain()},
        disabledChannels = this.disabledChannels
    )
}

fun TickerDto.toDomain() : Ticker {
    return Ticker(
        activo = this.activo,
        fechaDesde = this.fechaDesde,
        fechaHasta = this.fechaHasta,
        tiempoDuracion = this.tiempoDuracion,
        tiempoEntreApariciones = this.tiempoEntreApariciones,
        htmlUrl = this.htmlUrl
    )
}

private fun formatUrl(rawUrl: String?): String {
    if (rawUrl.isNullOrBlank()) return ""

    // 1. Limpiar espacios y asegurar protocolo https
    val baseUrl = if (rawUrl.startsWith("http", ignoreCase = true)) {
        rawUrl.trim()
    } else {
        "https://${rawUrl.trim()}"
    }

    // 2. Eliminar la barra diagonal final si existe para evitar doble barra (//)
    val cleanBaseUrl = baseUrl.removeSuffix("/")

    // 3. Añadir el path específico requerido
    return "$cleanBaseUrl/media_0.ts"
}