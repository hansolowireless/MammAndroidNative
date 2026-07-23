package com.mamm.mammapps.domain.model.player

data class TickerInfo(
    val tickers: List<Ticker> = emptyList()
)

data class Ticker(
    val campaignId: String? = null,
    val tiempoDuracion: Int = 0,
    val htmlUrl: String? = null
)
