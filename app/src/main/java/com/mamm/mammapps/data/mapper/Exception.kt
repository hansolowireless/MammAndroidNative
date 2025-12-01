package com.mamm.mammapps.data.mapper

import com.mamm.mammapps.data.model.exception.GetHomeContentException
import com.mamm.mammapps.data.model.exception.LoginException
import java.net.HttpURLConnection

fun Int.toGetHomeContentException(): GetHomeContentException {
    return when (this) {
        HttpURLConnection.HTTP_FORBIDDEN -> GetHomeContentException.ForbiddenException
        else -> GetHomeContentException.Generic("Error with response code: $this")
    }
}

fun Int.toLoginException(): LoginException {
    return when (this) {
        HttpURLConnection.HTTP_BAD_REQUEST -> LoginException.InvalidCredentials
        else -> LoginException.InvalidCredentials
    }
}