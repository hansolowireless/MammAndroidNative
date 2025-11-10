package com.mamm.mammapps.data.model.exception

import com.google.gson.annotations.SerializedName

/*Para parsear los errores en las respuestas
 que vienen de backend
 */
data class ErrorResponse(
    @SerializedName("message")
    val message: String? = null
)
