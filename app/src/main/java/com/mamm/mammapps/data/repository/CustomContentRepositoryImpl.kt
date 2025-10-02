package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.bookmark.GetBookmarksResponse
import com.mamm.mammapps.data.model.bookmark.SetBookmarkRequest
import com.mamm.mammapps.domain.interfaces.CustomContentRepository
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
            logger.error(TAG, "CustomContentRepositoryImpl Failed: ${it.message}, $it")
        }
    }

    override suspend fun setBookmark(bookmarkRequest: SetBookmarkRequest): Result<Unit> {
        return runCatching {
            remoteDatasource.setBookmark(bookmarkRequest = bookmarkRequest)
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

    override suspend fun getMostWatched(): Result<GetBookmarksResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getRecommended(): Result<GetBookmarksResponse> {
        TODO("Not yet implemented")
    }

}