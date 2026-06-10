package com.mamm.mammapps.domain.model.exception

sealed class SessionException : Exception() {
    data object Unauthorized : SessionException()
    data class Generic(override val message: String) : SessionException()
}
