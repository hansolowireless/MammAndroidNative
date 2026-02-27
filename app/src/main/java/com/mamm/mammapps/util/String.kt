package com.mamm.mammapps.util

import android.net.Uri
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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

fun parseSportEventDate(dateStr: String?): ZonedDateTime? {
    // Example date format: "20260227210000 +0100"
    val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss Z")
    if (dateStr.isNullOrBlank()) return null
    return try {
        ZonedDateTime.parse(dateStr, dateFormatter)
    } catch (e: DateTimeParseException) {
        null
    }
}