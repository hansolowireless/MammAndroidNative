package com.mamm.mammapps.domain.usecases.player.playprogresscache

import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import javax.inject.Inject

/**
 * UseCase encargado de recuperar el progreso de reproducción de forma SÍNCRONA.
 * Para evitar el uso de 'suspend' o 'Flow'
 * durante la inicialización del player.
 */
class GetPlayProgressUseCaseSync @Inject constructor(
    private val repository: PlaybackRepository
) {
    /**
     * Devuelve el valor actual de la caché al instante.
     */
    operator fun invoke(contentId: Int): Long {
        return repository.getContentProgress(contentId.toString())
    }
}