package com.mamm.mammapps.data.model.player;

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Ticker(
    val titulo: String,
    val textos: List<String>,
    val activo: Boolean,
    val fechaDesde: String,
    val fechaHasta: String,
    val tiempoDuracion: Int,
    val tiempoEntreApariciones: Int,
    val fondo: String
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Ticker {
            return Ticker(
                titulo = map["titulo"] as String,
                textos = (map["textos"] as List<*>).map { it as String },
                activo = map["activo"] as Boolean,
                fechaDesde = map["fecha_desde"] as String,
                fechaHasta = map["fecha_hasta"] as String,
                tiempoDuracion = map["tiempo_duracion"] as Int,
                tiempoEntreApariciones = map["tiempo_entre_apariciones"] as Int,
                fondo = map["fondo"] as String
            )
        }

        fun fromMapList(mapList: List<Map<String, Any>>): List<Ticker> {
            return mapList.map { fromMap(it) }
        }
    }

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