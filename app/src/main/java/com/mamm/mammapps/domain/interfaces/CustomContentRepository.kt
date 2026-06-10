package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.domain.model.bookmark.Bookmark
import com.mamm.mammapps.domain.model.recommended.RecommendedContent
import com.mamm.mammapps.ui.model.CustomizedContent

interface CustomContentRepository {
    suspend fun getBookmarks(): Result<List<Bookmark>>

    suspend fun deleteBookmark(contentId: Int, contentType: String): Result<Unit>

    suspend fun getMostWatched(): Result<List<Any>>

    suspend fun getRecommended(): Result<List<Any>>

    suspend fun getSimilar(subgenreId: Int) : Result<RecommendedContent>

    fun findContent(
        contentId: Int,
        contentType: CustomizedContent
    ): Result<Any>?

    suspend fun searchContent (query: String): Result<List<Bookmark>>

    suspend fun getTopChannels(): Result<com.mamm.mammapps.domain.model.topchannels.TopChannels>
}