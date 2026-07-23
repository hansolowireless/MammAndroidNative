package com.mamm.mammapps.data.model.player

import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.HomeFeaturedDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GetTickersResponseDto(
    @SerializedName("fecha_generacion")
    val fechaGeneracion: String? = null,

    @SerializedName("tickets")
    val tickers: List<TickerDto> = emptyList(),

    @SerializedName("disabled_channels")
    val disabledChannels: List<Int> = emptyList(),

    @SerializedName("operator_featured")
    val operatorFeatured: List<HomeFeaturedDto>? = null
)

data class TickerDto(

    @SerializedName("campaign_id")
    val campaignId: String? = null,

    @SerializedName("activo")
    val activo: Boolean = false,

    @SerializedName("fecha_desde")
    val fechaDesde: String? = null,

    @SerializedName("fecha_hasta")
    val fechaHasta: String? = null,

    @SerializedName("hora_desde")
    val horaDesde: String? = null,

    @SerializedName("hora_hasta")
    val horaHasta: String? = null,

    @SerializedName("tiempo_duracion")
    val tiempoDuracion: Int = 0,

    @SerializedName("tiempo_entre_apariciones")
    val tiempoEntreApariciones: Int = 0,

    @SerializedName("player_size")
    val playerSize: String? = null,

    @SerializedName("qr_url")
    val qrUrl: String? = null,

    @SerializedName("logo_url")
    val logoUrl: String? = null,

    @SerializedName("html_url")
    val htmlUrl: String? = null,

    @SerializedName("smarttv_url")
    val smartTvUrl: String? = null,

    @SerializedName("textos")
    val textos: List<String>? = null,

    @SerializedName("fondo")
    val fondo: String? = null

)