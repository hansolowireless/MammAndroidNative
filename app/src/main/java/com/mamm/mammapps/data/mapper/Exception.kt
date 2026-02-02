package com.mamm.mammapps.data.mapper

import com.mamm.mammapps.data.model.exception.GetHomeContentException
import com.mamm.mammapps.data.model.exception.GetMemoriesException
import com.mamm.mammapps.data.model.exception.LoginException
import com.mamm.mammapps.data.model.memories.GetMemoriesResponse
import java.net.HttpURLConnection

fun Int.toGetHomeContentException(): GetHomeContentException {
    return when (this) {
        HttpURLConnection.HTTP_FORBIDDEN -> GetHomeContentException.ForbiddenException
        else -> GetHomeContentException.Generic("Error with response code: $this")
    }
}

fun Int.toGetMemoriesException() : GetMemoriesException {
    return when (this) {
        HttpURLConnection.HTTP_FORBIDDEN -> GetMemoriesException.ForbiddenException
        HttpURLConnection.HTTP_NOT_FOUND -> GetMemoriesException.EmptyList
        else -> GetMemoriesException.Generic("Error with response code: $this")
    }
}

fun Int.toLoginException(): LoginException {
    return when (this) {
        HttpURLConnection.HTTP_BAD_REQUEST -> LoginException.InvalidCredentials
        else -> LoginException.InvalidCredentials
    }
}