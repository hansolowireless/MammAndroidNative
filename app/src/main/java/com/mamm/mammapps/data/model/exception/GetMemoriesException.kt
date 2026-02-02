package com.mamm.mammapps.data.model.exception

sealed class GetMemoriesException : Exception() {
    data object ForbiddenException : GetMemoriesException()
    data class Generic(override val message: String) : GetMemoriesException()
    data object EmptyList: GetMemoriesException()
}
