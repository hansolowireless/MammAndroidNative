package com.mamm.mammapps.domain.model.exception

sealed class TickerException : Exception () {
    data object MissingData : TickerException()
    data class Generic(override val message: String) : TickerException()
}
