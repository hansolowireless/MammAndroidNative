package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.bookmark.SetBookmarkRequest
import com.mamm.mammapps.data.model.mostwatched.MostWatchedContent
import com.mamm.mammapps.data.model.recommended.GetRecommendedResponse
import com.mamm.mammapps.ui.model.CustomizedContent

interface CustomContentRepository {
    suspend fun getBookmarks(): Result<List<Bookmark>>

    suspend fun saveBookmark(bookmarkRequest: SetBookmarkRequest): Result<Unit>

    suspend fun deleteBookmark(contentId: Int, contentType: String): Result<Unit>

    suspend fun getMostWatched(): Result<List<MostWatchedContent>>

    suspend fun getRecommended(): Result<List<Bookmark>>

    suspend fun getSimilar(subgenreId: Int) : Result<GetRecommendedResponse>

    fun findContent(
        contentId: Int,
        contentType: CustomizedContent
    ): Result<Any>?

    suspend fun searchContent (query: String): Result<List<Bookmark>>

}