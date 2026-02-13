package com.mamm.mammapps.data.mapper

import com.mamm.mammapps.data.model.diagnostic.DiagResponseDto
import com.mamm.mammapps.domain.model.DiagnosticInfo

fun DiagResponseDto.toDomain(): DiagnosticInfo {
    return DiagnosticInfo(
        node1Url = formatUrl(this.nodeHA.node01),
        node2Url = formatUrl(this.nodeHA.node02),
        node3Url = formatUrl(this.nodeDir.node01),
        node4Url = formatUrl(this.nodeDir.node02)
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