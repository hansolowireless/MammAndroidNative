package com.mamm.mammapps.data.model.player

import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.HomeFeaturedDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GetTickersResponseDto(
    @SerializedName("fecha_generacion")
    val fechaGeneracion: String,

    @SerializedName("tickets")
    val tickers: List<TickerDto>,

    @SerializedName("disabled_channels")
    val disabledChannels: List<Int> = emptyList(),

    @SerializedName("operator_featured")
    val operatorFeatured: List<HomeFeaturedDto>? = null
)

data class TickerDto(

    @SerializedName("activo")
    val activo: Boolean = false,

    @SerializedName("fecha_desde")
    val fechaDesde: String,

    @SerializedName("fecha_hasta")
    val fechaHasta: String,

    @SerializedName("tiempo_duracion")
    val tiempoDuracion: Int,

    @SerializedName("tiempo_entre_apariciones")
    val tiempoEntreApariciones: Int,

    @SerializedName("html_url")
    val htmlUrl: String? = null

)