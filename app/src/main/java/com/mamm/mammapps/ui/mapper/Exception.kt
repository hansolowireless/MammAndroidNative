package com.mamm.mammapps.ui.mapper

import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.exception.GetHomeContentException

fun Throwable.toResId() : Int {
    return when (this) {
        is GetHomeContentException -> this.toResId()
        else -> R.string.generic_error
    }
}

fun GetHomeContentException.toResId() : Int {
    return when (this) {
        is GetHomeContentException.ForbiddenException -> R.string.error_subscription_in_process
        is GetHomeContentException.GenericException -> R.string.error_content_generic
    }
}


