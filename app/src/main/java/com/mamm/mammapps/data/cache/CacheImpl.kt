package com.mamm.mammapps.data.cache

import com.mamm.mammapps.data.model.GetBrandedContentResponse
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.mostwatched.MostWatchedContent
import com.mamm.mammapps.data.model.recommended.GetRecommendedResponse
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheImpl @Inject constructor() : Cache {

    @Volatile
    private var cachedHomeContent: GetHomeContentResponse? = null

    @Volatile
    private var cachedMoviesContent: GetOtherContentResponse? = null

    @Volatile
    private var cachedDocumentariesContent: GetOtherContentResponse? = null

    @Volatile
    private var cachedSportsContent: GetOtherContentResponse? = null

    @Volatile
    private var cachedAdultsContent: GetBrandedContentResponse? = null

    @Volatile
    private var cachedKidsContent: GetOtherContentResponse? = null

    @Volatile
    private var cachedWarnerContent: GetBrandedContentResponse? = null

    @Volatile
    private var cachedAcontraContent: GetBrandedContentResponse? = null

    @Volatile
    private var cachedAMCContent: GetBrandedContentResponse? = null

    @Volatile
    private var cachedBookmarks: List<Bookmark>? = null

    @Volatile
    private var cachedMostWatched: List<MostWatchedContent>? = null

    @Volatile
    private var cachedRecommended: GetRecommendedResponse? = null

    @Volatile
    private var cachedLastTimePinWasCorrect: ZonedDateTime? = null

    override fun setHomeContent(homeContent: GetHomeContentResponse) {
        cachedHomeContent = homeContent
    }

    override fun setMoviesContent(moviesContent: GetOtherContentResponse) {
        cachedMoviesContent = moviesContent
    }

    override fun setDocumentariesContent(documentariesContent: GetOtherContentResponse) {
        cachedDocumentariesContent = documentariesContent
    }

    override fun setSportsContent(sportsContent: GetOtherContentResponse) {
        cachedSportsContent = sportsContent
    }

    override fun setKidsContent(kidsContent: GetOtherContentResponse) {
        cachedKidsContent = kidsContent
    }

    override fun setAdultsContent(adultsContent: GetBrandedContentResponse) {
        cachedAdultsContent = adultsContent
    }

    override fun setWarnerContent(warnerContent: GetBrandedContentResponse) {
        cachedWarnerContent = warnerContent
    }

    override fun setAcontraContent(acontraContent: GetBrandedContentResponse) {
        cachedAcontraContent = acontraContent
    }

    override fun setAMCContent(amcContent: GetBrandedContentResponse) {
        cachedAMCContent = amcContent
    }

    override fun setBookmarks(bookmarks: List<Bookmark>) {
        cachedBookmarks = bookmarks
    }

    override fun setMostWatched(mostWatched: List<MostWatchedContent>) {
        cachedMostWatched = mostWatched
    }

    override fun setRecommended(recommended: GetRecommendedResponse) {
        cachedRecommended = recommended
    }

    override fun setLastTimePinWasCorrect(lastTimePinWasCorrect: ZonedDateTime) {
        cachedLastTimePinWasCorrect = lastTimePinWasCorrect
    }


    override fun getHomeContent(): GetHomeContentResponse? = cachedHomeContent

    override fun getMoviesContent(): GetOtherContentResponse? = cachedMoviesContent

    override fun getDocumentariesContent(): GetOtherContentResponse? = cachedDocumentariesContent

    override fun getSportsContent(): GetOtherContentResponse? = cachedSportsContent

    override fun getKidsContent(): GetOtherContentResponse? = cachedKidsContent

    override fun getAdultsContent(): GetBrandedContentResponse? = cachedAdultsContent

    override fun getWarnerContent(): GetBrandedContentResponse? = cachedWarnerContent

    override fun getAcontraContent(): GetBrandedContentResponse? = cachedAcontraContent

    override fun getAMCContent(): GetBrandedContentResponse? = cachedAMCContent

    override fun getBookmarks(): List<Bookmark>? = cachedBookmarks

    override fun getMostWatched(): List<MostWatchedContent>? = cachedMostWatched

    override fun getRecommended(): GetRecommendedResponse? = cachedRecommended

    override fun getLastTimePinWasCorrect(): ZonedDateTime? = cachedLastTimePinWasCorrect

    override fun clear() {
        cachedHomeContent = null
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
    }

}