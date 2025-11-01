package com.mamm.mammapps.data.mapper

import com.mamm.mammapps.data.model.exception.GetHomeContentException
import java.net.HttpURLConnection

fun Int.toGetHomeContentException(): GetHomeContentException {
    return when (this) {
        HttpURLConnection.HTTP_FORBIDDEN -> GetHomeContentException.ForbiddenException
        else -> GetHomeContentException.GenericException("Error with response code: $this")
    }
}