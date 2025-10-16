package com.mamm.mammapps.util

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


fun isAndroidTV(context: Context): Boolean {
    return try {
        val uiModeManager = context.getSystemService(UiModeManager::class.java)
        uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
    } catch (e: Exception) {
        false
    }
}

/**
 * Comprueba si el dispositivo es considerado una tablet.
 *
 * El umbral común es de 600dp para el ancho de pantalla más pequeño.
 * @return `true` si es una tablet, `false` en caso contrario.
 */
fun Context.isTablet(): Boolean {
    val smallestScreenWidthDp = this.resources.configuration.smallestScreenWidthDp
    return smallestScreenWidthDp >= 600
}

fun Uri.replaceQueryParameter(key: String, newValue: String): Uri {
    val existingQuery = this.encodedQuery ?: ""
    val queryParams = mutableListOf<Pair<String, String>>()

    // Parsear los parámetros existentes
    if (existingQuery.isNotEmpty()) {
        existingQuery.split("&").forEach { param ->
            val parts = param.split("=", limit = 2)
            if (parts.size == 2) {
                val paramKey = Uri.decode(parts[0])
                val paramValue = Uri.decode(parts[1])
                if (paramKey != key) {
                    queryParams.add(paramKey to paramValue)
                }
            }
        }
    }

    // Agregar el nuevo parámetro
    queryParams.add(key to newValue)

    // Construir la nueva query string sin escapar los slashes
    val newQuery = queryParams.joinToString("&") { (k, v) ->
        "${Uri.encode(k, "/")}=${Uri.encode(v, "/")}"
    }

    return this.buildUpon()
        .clearQuery()
        .encodedQuery(newQuery)
        .build()
}

@OptIn(ExperimentalUuidApi::class)
fun Int?.orRandom() : Int {
    return this ?: Uuid.random().hashCode()
}