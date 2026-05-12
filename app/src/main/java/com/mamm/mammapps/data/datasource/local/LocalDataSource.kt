package com.mamm.mammapps.data.datasource.local

import com.mamm.mammapps.data.model.GetBrandedContentResponse
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.data.model.Subgenre
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.memories.GetMemoriesResponse
import com.mamm.mammapps.data.model.mostwatched.MostWatchedContent
import com.mamm.mammapps.data.model.recommended.GetRecommendedResponse
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface LocalDataSource {

    fun setShowBrandedContentMenus(show: Boolean)
    fun setLastTimePinWasCorrect(lastTimePinWasCorrect: ZonedDateTime)
    fun setContentPlayProgress(contentId: String, progress: Long)

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
    fun setMyMemories(memories: GetMemoriesResponse)
    fun setBookmarks(bookmarks: List<Bookmark>)
    fun setMostWatched(mostWatched: List<MostWatchedContent>)
    fun setRecommended(recommended: GetRecommendedResponse)

    fun getShowBrandedContentMenus () : Boolean?
    fun getLastTimePinWasCorrect(): ZonedDateTime?
    fun getDeviceSerial(): String
    fun getDeviceType(): String
    fun getChromecastDeviceType(): String
    fun getDrmBaseUrl(): String
    fun getDrmiV64(): ByteArray
    fun getDrmSecretKey64() : ByteArray
    fun getDrmJwtSecretKey() : ByteArray
    fun getApplicationVersion(): String
    fun getContentPlayProgress(contentId: String): Long
    fun getContentProgressFlow(): Flow<Map<String, Long>>

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
    fun getMyMemories(): GetMemoriesResponse?
    fun getBookmarks(): List<Bookmark>?
    fun getMostWatched(): List<MostWatchedContent>?
    fun getRecommended(): GetRecommendedResponse?

    fun clearContentPlayProgress()
    fun clearCache()

}