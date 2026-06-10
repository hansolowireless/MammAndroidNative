package com.mamm.mammapps.domain.model.exception

sealed class LoginException : Exception() {
    data object InvalidCredentials : LoginException()
    data class Generic(override val message: String) : LoginException()
}
