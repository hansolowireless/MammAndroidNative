package com.mamm.mammapps.domain.usecases.player

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import javax.inject.Inject

class SendBookmarkUseCase @Inject constructor (
    private val playbackRepository: PlaybackRepository,
    private val logger: Logger
){

    companion object {
        private const val TAG = "SendBookmarkUseCase"
    }

    suspend operator fun invoke(
        content: ContentToPlayUI,
        time: Long
    ): Result<Unit> {
        return runCatching {
            playbackRepository.setBookmark(
                content = content,
                time = time
            )
        }.onFailure {
            logger.error(TAG, "error sending bookmark $it.message")
        }
    }
}