package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.bookmark.GetBookmarksResponse
import com.mamm.mammapps.data.model.bookmark.SetBookmarkRequest
import com.mamm.mammapps.data.model.login.LocatorResponse
import com.mamm.mammapps.data.model.login.LoginResponse

interface CustomContentRepository {
    suspend fun getBookmarks(): Result<List<Bookmark>>

    suspend fun setBookmark(bookmarkRequest: SetBookmarkRequest): Result<Unit>

    suspend fun deleteBookmark(contentId: Int, contentType: String): Result<Unit>

    suspend fun getMostWatched(): Result<GetBookmarksResponse>

    suspend fun getRecommended(): Result<GetBookmarksResponse>
}