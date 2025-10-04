package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.bookmark.GetBookmarksResponse
import com.mamm.mammapps.data.model.bookmark.SetBookmarkRequest
import com.mamm.mammapps.data.model.mostwatched.MostWatchedContent
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

    override suspend fun saveBookmark(bookmarkRequest: SetBookmarkRequest): Result<Unit> {
        return runCatching {
            remoteDatasource.saveBookmark(bookmarkRequest = bookmarkRequest)
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

    override suspend fun getRecommended(): Result<GetBookmarksResponse> {
        TODO("Not yet implemented")
    }

    override fun findContent(
        contentId: Int,
        contentType: CustomizedContent
    ): Result<Any>? {
        val content: Any? = when (contentType) {
            CustomizedContent.BookmarkType -> remoteDatasource.getCachedBookmarks().find { it.id == contentId }
            CustomizedContent.MostWatchedType -> remoteDatasource.getCachedMostWatched().find { it.id == contentId }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

}