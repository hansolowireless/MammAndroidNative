package com.mamm.mammapps.util

import android.net.Uri

/**Cleans the URl String from
 * @parameterName value*/
fun cleanUrl(url: String, parameterName: String): String {
    return try {
        val uri = Uri.parse(url)
        val path = uri.path ?: "/"

        // Obtener todos los query parameters excepto 'stoken'
        val queryParams = mutableListOf<String>()
        uri.queryParameterNames?.forEach { paramName ->
            if (paramName != parameterName) {
                uri.getQueryParameter(paramName)?.let { paramValue ->
                    queryParams.add("$paramName=$paramValue")
                }
            }
        }

        // Construir la URL final
        if (queryParams.isNotEmpty()) {
            "$path?${queryParams.joinToString("&")}"
        } else {
            path
        }
    } catch (e: Exception) {
        "/"
    }
}