package com.mamm.mammapps.ui.mapper

import com.google.android.exoplayer2.PlaybackException
import com.mamm.mammapps.ui.model.player.PlayerErrorType
import javax.inject.Inject

/**
 * Clasifica un [PlaybackException] de ExoPlayer en un [PlayerErrorType] y decide
 * si el error es transitorio (merece la pena reintentar).
 *
 * Rangos de códigos de ExoPlayer:
 *  - 2000-2999: entrada/salida (carga de la fuente, red).
 *  - 4000-4999: decodificación.
 *  - 6000-6999: DRM.
 */
class PlayerErrorMapper @Inject constructor() {

    /**
     * Los "source errors" (red y carga de la fuente) son recuperables, salvo los que
     * indican que el contenido simplemente no está disponible.
     */
    fun isTransient(exception: PlaybackException): Boolean {
        val code = exception.errorCode
        if (code in UNAVAILABLE_ERROR_CODES) return false
        return code == PlaybackException.ERROR_CODE_TIMEOUT || code in IO_ERROR_RANGE
    }

    fun map(exception: PlaybackException): PlayerErrorType = when {
        isTransient(exception) -> PlayerErrorType.TRANSIENT
        exception.errorCode in UNAVAILABLE_ERROR_CODES -> PlayerErrorType.UNAVAILABLE
        exception.errorCode in DRM_ERROR_RANGE -> PlayerErrorType.DRM
        exception.errorCode in DECODER_ERROR_RANGE -> PlayerErrorType.DECODER
        else -> PlayerErrorType.GENERIC
    }

    private companion object {
        val IO_ERROR_RANGE = 2000..2999
        val DECODER_ERROR_RANGE = 4000..4999
        val DRM_ERROR_RANGE = 6000..6999

        // Errores de fuente que NO se arreglan reintentando: contenido no disponible.
        val UNAVAILABLE_ERROR_CODES = setOf(
            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND,
            PlaybackException.ERROR_CODE_IO_NO_PERMISSION,
            PlaybackException.ERROR_CODE_IO_CLEARTEXT_NOT_PERMITTED,
        )
    }
}
