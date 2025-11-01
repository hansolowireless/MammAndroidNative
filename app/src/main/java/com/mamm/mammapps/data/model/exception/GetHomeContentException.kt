package com.mamm.mammapps.data.model.exception

sealed class GetHomeContentException : Exception() {
    data class GenericException(override val message: String) : GetHomeContentException()
    data object ForbiddenException : GetHomeContentException()
}