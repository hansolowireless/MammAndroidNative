package com.mamm.mammapps.domain.usecases.player.playprogresscache

import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * UseCase encargado de recuperar el progreso de reproducción de un contenido desde la memoria (Caché).
 * Si no existe progreso previo, devolverá 0L.
 */
class GetPlayProgressUseCase @Inject constructor(
    private val repository: PlaybackRepository
) {
    /**
     * Devuelve un Flow que emite el progreso actual del contenido.
     * Se actualizará automáticamente si el Player guarda un nuevo progreso.
     */
    operator fun invoke(contentId: Int): Flow<Long> {
        return repository.getContentProgressFlow().map { map ->
            map[contentId.toString()] ?: 0L
        }
    }
}