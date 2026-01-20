package com.mamm.mammapps.data.cache

import com.mamm.mammapps.data.model.GetBrandedContentResponse
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.data.model.Subgenre
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.mostwatched.MostWatchedContent
import com.mamm.mammapps.data.model.recommended.GetRecommendedResponse
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface Cache {

    fun setShowBrandedContentMenus(show: Boolean)

    fun setHomeContent(homeContent: GetHomeContentResponse)
    fun setCachedSubgenreList(list: List<Subgenre>)
    fun setMoviesContent(moviesContent: GetOtherContentResponse)
    fun setDocumentariesContent(documentariesContent: GetOtherContentResponse)
    fun setSportsContent(sportsContent: GetOtherContentResponse)
    fun setKidsContent(kidsContent: GetOtherContentResponse)
    fun setAdultsContent(adultsContent: GetBrandedContentResponse)
    fun setWarnerContent(warnerContent: GetBrandedContentResponse)
    fun setAcontraContent(acontraContent: GetBrandedContentResponse)
    fun setAMCContent(amcContent: GetBrandedContentResponse)

    fun getShowBrandedContentMenus () : Boolean?
    fun getHomeContent(): GetHomeContentResponse?
    fun getCachedSubgenreList(): List<Subgenre>?
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
    fun setContentPlayProgress(id: String, progress: Long)

    fun getBookmarks(): List<Bookmark>?
    fun getMostWatched(): List<MostWatchedContent>?
    fun getRecommended(): GetRecommendedResponse?
    fun getLastTimePinWasCorrect(): ZonedDateTime?
    fun getContentPlayProgress(id: String): Long
    fun getProgressFlow(): Flow<Map<String, Long>>

    fun clearContentPlayProgress()
    fun clear()

}