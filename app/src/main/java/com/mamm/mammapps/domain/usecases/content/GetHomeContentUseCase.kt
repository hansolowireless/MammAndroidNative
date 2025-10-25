package com.mamm.mammapps.domain.usecases.content

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.CustomContentRepository
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.mapper.insertBookmarks
import com.mamm.mammapps.ui.mapper.insertFeatured
import com.mamm.mammapps.ui.mapper.insertMostWatched
import com.mamm.mammapps.ui.mapper.insertRecommended
import com.mamm.mammapps.ui.mapper.toContentUIRows
import com.mamm.mammapps.ui.model.ContentRowUI
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class GetHomeContentUseCase @Inject constructor(
    private val repository: MammRepository,
    private val customContentRepository: CustomContentRepository,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "GetHomeContentUseCase"
    }

    suspend operator fun invoke(): Result<List<ContentRowUI>> {
        return try {
            supervisorScope {
                val homeContent = async { repository.getHomeContent() }
                val bookmarks = async { customContentRepository.getBookmarks() }
                val mostWatched = async { customContentRepository.getMostWatched() }
                val recommended = async { customContentRepository.getRecommended() }

                //Wait for all to complete
                val homeResult = homeContent.await()
                val bookmarksResult = bookmarks.await()
                val mostWatchedResult = mostWatched.await()
                val recommendedResult = recommended.await()

                // Now check if home content succeeded (it's required)
                if (homeResult.isFailure) {
                    logger.error(TAG, "GetHomeContentUseCase: $homeResult")
                    return@supervisorScope Result.failure(
                        homeResult.exceptionOrNull() ?: Exception("Failed to load home content")
                    )
                }

                // Use successful results, fallback for optional content
                // Bookmarks y recomendados tienen la misma estructura de json
                val contentRows = homeResult.getOrThrow().toContentUIRows()
                    .insertBookmarks(bookmarksResult.getOrElse { emptyList() })
                    .insertRecommended(recommendedResult.getOrElse { emptyList() })
                    .insertMostWatched(mostWatchedResult.getOrElse { emptyList() })
                    .insertFeatured(homeResult.getOrThrow().featured.orEmpty())

                Result.success(contentRows)
            }
        } catch (e: CancellationException) {
            logger.debug(TAG, "GetHomeContentUseCase was cancelled")
            throw e
        } catch (e: Exception) {
            logger.error(TAG, "GetHomeContentUseCase Failed: ${e.message}")
            Result.failure(e)
        }
    }

}