package com.mamm.mammapps.data.cache

import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.mostwatched.MostWatchedContent
import com.mamm.mammapps.data.model.recommended.GetRecommendedResponse
import java.time.ZonedDateTime

interface Cache {

    fun setBookmarks(bookmarks: List<Bookmark>)
    fun setMostWatched(mostWatched: List<MostWatchedContent>)
    fun setRecommended(recommended: GetRecommendedResponse)
    fun setLastTimePinWasCorrect(lastTimePinWasCorrect: ZonedDateTime)

    fun getBookmarks(): List<Bookmark>?
    fun getMostWatched(): List<MostWatchedContent>?
    fun getRecommended(): GetRecommendedResponse?
    fun getLastTimePinWasCorrect(): ZonedDateTime?

}