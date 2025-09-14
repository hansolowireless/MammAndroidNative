package com.mamm.mammapps.domain.usecases.player

import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import javax.inject.Inject

class GetDRMUrlUseCase @Inject constructor (
    private val playbackRepository: PlaybackRepository
){
    suspend operator fun invoke(content: ContentToPlayUI): Result<String> {
        return playbackRepository.getDRMUrl(content = content)
            .map { pair -> pair.first }
    }
}