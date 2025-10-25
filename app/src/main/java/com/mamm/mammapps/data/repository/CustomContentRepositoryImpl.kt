package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.bookmark.Recommended
import com.mamm.mammapps.data.model.mostwatched.MostWatchedContent
import com.mamm.mammapps.data.model.recommended.GetRecommendedResponse
import com.mamm.mammapps.domain.interfaces.CustomContentRepository
import com.mamm.mammapps.ui.model.CustomizedContent
import javax.inject.Inject

class CustomContentRepositoryImpl @Inject constructor(
    private val remoteDatasource: RemoteDatasource,
    private val logger: Logger
) : CustomContentRepository {

    companion object {
        private const val TAG = "CustomContentRepositoryImpl"
    }

    override suspend fun getBookmarks(): Result<List<Bookmark>> {
        return runCatching {
            remoteDatasource.getBookmarks()
        }.onFailure {
            logger.error(TAG, "getBookmarks failed: ${it.message}, $it")
        }
    }

    override suspend fun deleteBookmark(contentId: Int, contentType: String): Result<Unit> {
        return runCatching {
            remoteDatasource.deleteBookmark(
                contentId = contentId,
                contentType = contentType
            )
        }
    }

    override suspend fun getMostWatched(): Result<List<MostWatchedContent>> {
        return runCatching {
            remoteDatasource.getMostWatched()
        }.onFailure {
            logger.error(TAG, "getMostWatched failed: ${it.message}, $it")
        }
    }

    override suspend fun getRecommended(): Result<List<Recommended>> {
        return runCatching {
            remoteDatasource.getRecommended().let { response ->
                val vods = response.vods.orEmpty()
                val cutvs = response.cutvs.orEmpty()
                vods + cutvs
            }
        }.onFailure {
            logger.error(TAG, "getRecommended failed: ${it.message}, $it")
        }
    }

    override suspend fun getSimilar(subgenreId: Int): Result<GetRecommendedResponse> {
        return runCatching {
            remoteDatasource.getSimilarContent(subgenreId = subgenreId)
        }.onFailure {
            logger.error(TAG, "getSimilar failed: ${it.message}, $it")
        }
    }

    override fun findContent(
        contentId: Int,
        contentType: CustomizedContent
    ): Result<Any>? {
        val content: Any? = when (contentType) {
            CustomizedContent.BookmarkType -> remoteDatasource.getCachedBookmarks().find { it.id == contentId }
            CustomizedContent.MostWatchedType -> remoteDatasource.getCachedMostWatched().find { it.id == contentId }
            CustomizedContent.RecommendedType -> remoteDatasource.getCachedRecommended()?.vods?.find { it.id == contentId } ?: remoteDatasource.getCachedRecommended()?.cutvs?.find { it.id == contentId }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override suspend fun searchContent (query: String): Result<List<Bookmark>> {
        return runCatching {
            remoteDatasource.search(query = query)
        }.onFailure {
            logger.error(TAG, "searchContent failed: ${it.message}, $it")
        }
    }

}