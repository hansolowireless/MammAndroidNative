package com.mamm.mammapps.data.util

fun formatNodeUrl(rawUrl: String?): String {
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