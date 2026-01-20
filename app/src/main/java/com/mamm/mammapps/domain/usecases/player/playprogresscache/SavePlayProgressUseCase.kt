package com.mamm.mammapps.domain.usecases.player.playprogresscache


import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import javax.inject.Inject

/**
 * UseCase encargado de guardar el progreso de reproducción de un contenido.
 * Este progreso se almacenará únicamente en memoria (Caché) durante la sesión actual.
 */
class SavePlayProgressUseCase @Inject constructor(
    private val repository: PlaybackRepository
) {
    operator fun invoke(contentId: String, progress: Long) {
        if (contentId.isNotEmpty()) {
            repository.saveContentProgress(contentId, progress)
        }
    }
}