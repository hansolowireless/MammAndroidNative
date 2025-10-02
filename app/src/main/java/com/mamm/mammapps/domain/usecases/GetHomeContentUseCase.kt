package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.CustomContentRepository
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.mapper.insertBookmarks
import com.mamm.mammapps.ui.mapper.toContentUIRows
import com.mamm.mammapps.ui.model.ContentRowUI
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetHomeContentUseCase @Inject constructor(
    private val repository: MammRepository,
    private val customContentRepository: CustomContentRepository,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "GetHomeContentUseCase"
    }

    suspend operator fun invoke(): Result<List<ContentRowUI>> {
        return runCatching {
            coroutineScope {
                val homeContent = async { repository.getHomeContent().getOrThrow() }
                val bookmarks = async { customContentRepository.getBookmarks().getOrThrow() }
                val recommended = async { customContentRepository.getRecommended().getOrThrow() }
                val mostWatched = async { customContentRepository.getMostWatched().getOrThrow() }

                homeContent.await().toContentUIRows()
                    .insertBookmarks(bookmarks.await())
                    .insertBookmarks(recommended.await())
                    .insertBookmarks(mostWatched.await())
            }
        }.onSuccess {
            logger.debug(TAG, "GetHomeContentUseCase Received successful response")
        }.onFailure { exception ->
            logger.error(TAG, "GetHomeContentUseCase Failed: ${exception.message}, $exception")
        }
    }

}