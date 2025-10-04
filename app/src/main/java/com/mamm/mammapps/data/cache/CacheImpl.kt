package com.mamm.mammapps.data.cache

import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.mostwatched.MostWatchedContent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheImpl @Inject constructor() : Cache {

    @Volatile
    private var cachedBookmarks: List<Bookmark>? = null

    @Volatile
    private var cachedMostWatched: List<MostWatchedContent>? = null

    override fun getBookmarks(): List<Bookmark>? {
        return cachedBookmarks
    }

    override fun getMostWatched(): List<MostWatchedContent>? {
        return cachedMostWatched
    }

    override fun setBookmarks(bookmarks: List<Bookmark>) {
        cachedBookmarks = bookmarks
    }

    override fun setMostWatched(mostWatched: List<MostWatchedContent>) {
        cachedMostWatched = mostWatched
    }

}