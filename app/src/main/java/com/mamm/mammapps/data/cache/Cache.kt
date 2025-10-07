package com.mamm.mammapps.data.cache

import com.mamm.mammapps.data.model.GetBrandedContentResponse
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.mostwatched.MostWatchedContent
import com.mamm.mammapps.data.model.recommended.GetRecommendedResponse
import java.time.ZonedDateTime

interface Cache {

    fun setHomeContent(homeContent: GetHomeContentResponse)
    fun setMoviesContent(moviesContent: GetOtherContentResponse)
    fun setDocumentariesContent(documentariesContent: GetOtherContentResponse)
    fun setSportsContent(sportsContent: GetOtherContentResponse)
    fun setKidsContent(kidsContent: GetOtherContentResponse)
    fun setAdultsContent(adultsContent: GetBrandedContentResponse)
    fun setWarnerContent(warnerContent: GetBrandedContentResponse)
    fun setAcontraContent(acontraContent: GetBrandedContentResponse)
    fun setAMCContent(amcContent: GetBrandedContentResponse)

    fun getHomeContent(): GetHomeContentResponse?
    fun getMoviesContent(): GetOtherContentResponse?
    fun getDocumentariesContent(): GetOtherContentResponse?
    fun getSportsContent(): GetOtherContentResponse?
    fun getKidsContent(): GetOtherContentResponse?
    fun getAdultsContent(): GetBrandedContentResponse?
    fun getWarnerContent(): GetBrandedContentResponse?
    fun getAcontraContent(): GetBrandedContentResponse?
    fun getAMCContent(): GetBrandedContentResponse?
    fun setBookmarks(bookmarks: List<Bookmark>)
    fun setMostWatched(mostWatched: List<MostWatchedContent>)
    fun setRecommended(recommended: GetRecommendedResponse)
    fun setLastTimePinWasCorrect(lastTimePinWasCorrect: ZonedDateTime)

    fun getBookmarks(): List<Bookmark>?
    fun getMostWatched(): List<MostWatchedContent>?
    fun getRecommended(): GetRecommendedResponse?
    fun getLastTimePinWasCorrect(): ZonedDateTime?

    fun clear()

}