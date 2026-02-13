package com.mamm.mammapps.domain.model

/**
 * Contiene las URLs ya limpias y listas para usar de los 4 nodos de diagnóstico.
 * Este es el modelo agnóstico que usará el ViewModel y la UI.
 */
data class DiagnosticInfo(
    val node1Url: String,
    val node2Url: String,
    val node3Url: String,
    val node4Url: String
)