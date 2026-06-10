package com.mamm.mammapps.data.cache

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

interface Cache {

    fun setShowBrandedContentMenus(show: Boolean)

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

    fun getShowBrandedContentMenus () : Boolean?
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

    fun setBookmarks(bookmarks: List<BookmarkDto>)
    fun setMostWatched(mostWatched: List<MostWatchedContentDto>)
    fun setRecommended(recommended: GetRecommendedResponseDto)
    fun setLastTimePinWasCorrect(lastTimePinWasCorrect: ZonedDateTime)
    fun setContentPlayProgress(id: String, progress: Long)

    fun getBookmarks(): List<BookmarkDto>?
    fun getMostWatched(): List<MostWatchedContentDto>?
    fun getRecommended(): GetRecommendedResponseDto?
    fun getLastTimePinWasCorrect(): ZonedDateTime?
    fun getContentPlayProgress(id: String): Long
    fun getProgressFlow(): Flow<Map<String, Long>>

    fun clearContentPlayProgress()
    fun clear()

}