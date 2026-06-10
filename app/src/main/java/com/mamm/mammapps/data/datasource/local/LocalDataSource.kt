package com.mamm.mammapps.data.datasource.local

import com.mamm.mammapps.data.model.GetBrandedContentResponseDto
import com.mamm.mammapps.data.model.GetHomeContentResponseDto
import com.mamm.mammapps.data.model.GetOtherContentResponseDto
import com.mamm.mammapps.data.model.SubgenreDto
import com.mamm.mammapps.data.model.bookmark.BookmarkDto
import com.mamm.mammapps.data.model.memories.GetMemoriesResponseDto
import com.mamm.mammapps.data.model.mostwatched.MostWatchedContentDto
import com.mamm.mammapps.data.model.recommended.GetRecommendedResponseDto
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface LocalDataSource {

    fun setShowBrandedContentMenus(show: Boolean)
    fun setLastTimePinWasCorrect(lastTimePinWasCorrect: ZonedDateTime)
    fun setContentPlayProgress(contentId: String, progress: Long)

    fun setHomeContent(homeContent: GetHomeContentResponseDto)
    fun setCachedSubgenreList(list: List<SubgenreDto>)
    fun setMoviesContent(moviesContent: GetOtherContentResponseDto)
    fun setDocumentariesContent(documentariesContent: GetOtherContentResponseDto)
    fun setSportsContent(sportsContent: GetOtherContentResponseDto)
    fun setKidsContent(kidsContent: GetOtherContentResponseDto)
    fun setAdultsContent(adultsContent: GetBrandedContentResponseDto)
    fun setWarnerContent(warnerContent: GetBrandedContentResponseDto)
    fun setAcontraContent(acontraContent: GetBrandedContentResponseDto)
    fun setAMCContent(amcContent: GetBrandedContentResponseDto)
    fun setMyMemories(memories: GetMemoriesResponseDto)
    fun setBookmarks(bookmarks: List<BookmarkDto>)
    fun setMostWatched(mostWatched: List<MostWatchedContentDto>)
    fun setRecommended(recommended: GetRecommendedResponseDto)

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

    fun getHomeContent(): GetHomeContentResponseDto?
    fun getCachedSubgenreList(): List<SubgenreDto>?
    fun getMoviesContent(): GetOtherContentResponseDto?
    fun getDocumentariesContent(): GetOtherContentResponseDto?
    fun getSportsContent(): GetOtherContentResponseDto?
    fun getKidsContent(): GetOtherContentResponseDto?
    fun getAdultsContent(): GetBrandedContentResponseDto?
    fun getWarnerContent(): GetBrandedContentResponseDto?
    fun getAcontraContent(): GetBrandedContentResponseDto?
    fun getAMCContent(): GetBrandedContentResponseDto?
    fun getMyMemories(): GetMemoriesResponseDto?
    fun getBookmarks(): List<BookmarkDto>?
    fun getMostWatched(): List<MostWatchedContentDto>?
    fun getRecommended(): GetRecommendedResponseDto?

    fun clearContentPlayProgress()
    fun clearCache()

}