package com.mamm.mammapps.domain.model.exception

sealed class GetHomeContentException : Exception() {
    data object ForbiddenException : GetHomeContentException()
    data class Generic(override val message: String) : GetHomeContentException()
}
