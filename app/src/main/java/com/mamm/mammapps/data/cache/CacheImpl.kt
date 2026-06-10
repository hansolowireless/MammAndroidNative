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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheImpl @Inject constructor() : Cache {

    @Volatile
    private var showBrandedContentMenus: Boolean? = null

    @Volatile
    private var cachedHomeContent: GetHomeContentResponseDto? = null

    @Volatile
    private var cachedSubgenreList: List<SubgenreDto>? = null

    @Volatile
    private var cachedMoviesContent: GetOtherContentResponseDto? = null

    @Volatile
    private var cachedDocumentariesContent: GetOtherContentResponseDto? = null

    @Volatile
    private var cachedSportsContent: GetOtherContentResponseDto? = null

    @Volatile
    private var cachedAdultsContent: GetBrandedContentResponseDto? = null

    @Volatile
    private var cachedKidsContent: GetOtherContentResponseDto? = null

    @Volatile
    private var cachedWarnerContent: GetBrandedContentResponseDto? = null

    @Volatile
    private var cachedAcontraContent: GetBrandedContentResponseDto? = null

    @Volatile
    private var cachedAMCContent: GetBrandedContentResponseDto? = null

    @Volatile
    private var cachedMyMemories: GetMemoriesResponseDto? = null

    @Volatile
    private var cachedBookmarks: List<BookmarkDto>? = null

    @Volatile
    private var cachedMostWatched: List<MostWatchedContentDto>? = null

    @Volatile
    private var cachedRecommended: GetRecommendedResponseDto? = null

    @Volatile
    private var cachedLastTimePinWasCorrect: ZonedDateTime? = null

    @Volatile
    private var cachedContentProgress = MutableStateFlow<Map<String, Long>>(emptyMap())

    override fun setShowBrandedContentMenus(show: Boolean) {
        showBrandedContentMenus = show
    }

    override fun setHomeContent(homeContent: GetHomeContentResponseDto) {
        cachedHomeContent = homeContent
    }

    override fun setCachedSubgenreList(list: List<SubgenreDto>) {
        cachedSubgenreList = list
    }

    override fun setMoviesContent(moviesContent: GetOtherContentResponseDto) {
        cachedMoviesContent = moviesContent
    }

    override fun setDocumentariesContent(documentariesContent: GetOtherContentResponseDto) {
        cachedDocumentariesContent = documentariesContent
    }

    override fun setSportsContent(sportsContent: GetOtherContentResponseDto) {
        cachedSportsContent = sportsContent
    }

    override fun setKidsContent(kidsContent: GetOtherContentResponseDto) {
        cachedKidsContent = kidsContent
    }

    override fun setAdultsContent(adultsContent: GetBrandedContentResponseDto) {
        cachedAdultsContent = adultsContent
    }

    override fun setWarnerContent(warnerContent: GetBrandedContentResponseDto) {
        cachedWarnerContent = warnerContent
    }

    override fun setAcontraContent(acontraContent: GetBrandedContentResponseDto) {
        cachedAcontraContent = acontraContent
    }

    override fun setAMCContent(amcContent: GetBrandedContentResponseDto) {
        cachedAMCContent = amcContent
    }

    override fun setMyMemories(memories: GetMemoriesResponseDto) {
        cachedMyMemories = memories
    }

    override fun setBookmarks(bookmarks: List<BookmarkDto>) {
        cachedBookmarks = bookmarks
    }

    override fun setMostWatched(mostWatched: List<MostWatchedContentDto>) {
        cachedMostWatched = mostWatched
    }

    override fun setRecommended(recommended: GetRecommendedResponseDto) {
        cachedRecommended = recommended
    }

    override fun setLastTimePinWasCorrect(lastTimePinWasCorrect: ZonedDateTime) {
        cachedLastTimePinWasCorrect = lastTimePinWasCorrect
    }

    override fun setContentPlayProgress(id: String, progress: Long) {
        cachedContentProgress.update { it + (id to progress) }
    }

    override fun getShowBrandedContentMenus(): Boolean? = showBrandedContentMenus

    override fun getHomeContent(): GetHomeContentResponseDto? = cachedHomeContent

    override fun getCachedSubgenreList(): List<SubgenreDto>? = cachedSubgenreList

    override fun getMoviesContent(): GetOtherContentResponseDto? = cachedMoviesContent

    override fun getDocumentariesContent(): GetOtherContentResponseDto? = cachedDocumentariesContent

    override fun getSportsContent(): GetOtherContentResponseDto? = cachedSportsContent

    override fun getKidsContent(): GetOtherContentResponseDto? = cachedKidsContent

    override fun getAdultsContent(): GetBrandedContentResponseDto? = cachedAdultsContent

    override fun getWarnerContent(): GetBrandedContentResponseDto? = cachedWarnerContent

    override fun getAcontraContent(): GetBrandedContentResponseDto? = cachedAcontraContent

    override fun getAMCContent(): GetBrandedContentResponseDto? = cachedAMCContent

    override fun getMyMemories(): GetMemoriesResponseDto? = cachedMyMemories

    override fun getBookmarks(): List<BookmarkDto>? = cachedBookmarks

    override fun getMostWatched(): List<MostWatchedContentDto>? = cachedMostWatched

    override fun getRecommended(): GetRecommendedResponseDto? = cachedRecommended

    override fun getLastTimePinWasCorrect(): ZonedDateTime? = cachedLastTimePinWasCorrect

    override fun getContentPlayProgress(id: String): Long {
        return cachedContentProgress.value[id] ?: 0L
    }

    override fun getProgressFlow(): Flow<Map<String, Long>> = cachedContentProgress.asStateFlow()

    override fun clearContentPlayProgress() {
        cachedContentProgress.value = emptyMap()
    }

    override fun clear() {
        cachedHomeContent = null
        cachedSubgenreList = null
        cachedMoviesContent = null
        cachedDocumentariesContent = null
        cachedSportsContent = null
        cachedAdultsContent = null
        cachedKidsContent = null
        cachedWarnerContent = null
        cachedAcontraContent = null
        cachedAMCContent = null
        cachedBookmarks = null
        cachedMostWatched = null
        cachedRecommended = null
        cachedLastTimePinWasCorrect = null
        clearContentPlayProgress()
    }

}