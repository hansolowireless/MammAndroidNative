package com.mamm.mammapps.ui.mapper

import com.mamm.mammapps.R
import com.mamm.mammapps.domain.model.exception.GetHomeContentException
import com.mamm.mammapps.domain.model.exception.GetMemoriesException
import com.mamm.mammapps.domain.model.exception.LoginException

fun GetHomeContentException.toResId(): Int {
    return when (this) {
        is GetHomeContentException.ForbiddenException -> R.string.error_subscription_in_process
        is GetHomeContentException.Generic -> R.string.error_content_generic
    }
}

fun GetMemoriesException.toResId(): Int {
    return when (this) {
        is GetMemoriesException.EmptyList,
        is GetMemoriesException.ForbiddenException -> R.string.error_memories_no_content
        is GetMemoriesException.Generic -> R.string.error_content_generic
    }
}

fun LoginException.toResId(): Int {
    return when (this) {
        is LoginException.InvalidCredentials -> R.string.error_invalid_credentials
        is LoginException.Generic -> R.string.error_content_generic
    }
}

fun Throwable.toResId(): Int {
    return when (this) {
        is GetHomeContentException -> toResId()
        is LoginException -> toResId()
        is GetMemoriesException -> toResId()
        else -> R.string.error_content_generic
    }
}


