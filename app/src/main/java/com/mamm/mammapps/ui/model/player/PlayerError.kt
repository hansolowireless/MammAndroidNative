package com.mamm.mammapps.ui.model.player

/**
 * Tipos de error del player, para mapearlos a un mensaje localizado en la UI
 * y decidir la estrategia de reintento.
 */
enum class PlayerErrorType {
    /** Errores de red / carga de la fuente (source), recuperables → se reintenta. */
    TRANSIENT,

    /** Error de licencia / DRM. */
    DRM,

    /** Error de decodificación o formato no soportado por el dispositivo. */
    DECODER,

    /** Contenido no disponible (no encontrado, sin permiso): reintentar no ayuda. */
    UNAVAILABLE,

    /** Resto de errores. */
    GENERIC
}
