package com.mamm.mammapps.domain.model.player

import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.domain.model.entity.Featured
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TickerInfo (
    val fechaGeneracion: String,
    val tickers: List<Ticker>,
    val disabledChannels: List<Int> = emptyList(),
    val operatorFeatured: List<Featured> = emptyList()
)

data class Ticker (
    val activo: Boolean,
    val fechaDesde: String,
    val fechaHasta: String,
    val tiempoDuracion: Int,
    val tiempoEntreApariciones: Int,
    val htmlUrl: String? = null
) {
    /**
     * Valida si el ticker es válido basado en:
     * - Debe estar activo (activo = true)
     * - La fecha actual debe estar entre fechaDesde y fechaHasta
     */
    fun isValid(): Boolean {
        if (!activo) return false
        if (htmlUrl == null) return false

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