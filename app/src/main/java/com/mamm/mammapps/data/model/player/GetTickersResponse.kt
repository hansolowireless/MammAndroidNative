package com.mamm.mammapps.data.model.player

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GetTickersResponse(
    @SerializedName("fecha_generacion")
    val fechaGeneracion: String,

    @SerializedName("tickets")
    val tickers: List<Ticker>
)

data class Ticker(
    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("textos")
    val textos: List<String>,

    @SerializedName("activo")
    val activo: Boolean,

    @SerializedName("fecha_desde")
    val fechaDesde: String,

    @SerializedName("fecha_hasta")
    val fechaHasta: String,

    @SerializedName("fondo")
    val fondo: String,

    @SerializedName("tiempo_duracion")
    val tiempoDuracion: Int,

    @SerializedName("tiempo_entre_apariciones")
    val tiempoEntreApariciones: Int
) {
    /**
     * Valida si el ticker es válido basado en:
     * - Debe estar activo (activo = true)
     * - La fecha actual debe estar entre fechaDesde y fechaHasta
     */
    fun isValid(): Boolean {
        if (!activo) return false

        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val ahora = Date()
            val desde = formatter.parse(fechaDesde)
            val hasta = formatter.parse(fechaHasta)

            desde != null && hasta != null &&
                    ahora.after(desde) && ahora.before(hasta)
        } catch (e: Exception) {
            // Si hay error parseando las fechas, considerar inválido
            false
        }
    }
}