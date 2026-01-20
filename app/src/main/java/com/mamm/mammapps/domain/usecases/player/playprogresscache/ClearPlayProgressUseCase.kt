package com.mamm.mammapps.domain.usecases.player.playprogresscache

import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import javax.inject.Inject

/**
 * UseCase encargado de limpiar todo el progreso de reproducción almacenado en memoria.
 */
class ClearPlayProgressUseCase @Inject constructor(
    private val repository: PlaybackRepository
) {
    operator fun invoke() {
        repository.clearContentProgress()
    }
}