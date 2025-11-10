package com.mamm.mammapps.ui.mapper

import com.google.gson.Gson
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.exception.ErrorResponse
import com.mamm.mammapps.data.model.exception.GetHomeContentException
import retrofit2.HttpException

fun GetHomeContentException.toResId() : Int {
    return when (this) {
        is GetHomeContentException.ForbiddenException -> R.string.error_subscription_in_process
        is GetHomeContentException.GenericException -> R.string.error_content_generic
    }
}

fun HttpException?.toResponseBodyMessage() : String {
    val result = runCatching {
        val errorBody = this?.response()?.errorBody()?.string()
        Gson().fromJson(errorBody, ErrorResponse::class.java).message
    }
    return result.getOrNull() ?: this?.message().orEmpty()
}


