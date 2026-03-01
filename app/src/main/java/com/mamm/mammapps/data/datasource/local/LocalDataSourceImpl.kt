package com.mamm.mammapps.data.datasource.local

import com.mamm.mammapps.BuildConfig
import com.mamm.mammapps.data.cache.Cache
import com.mamm.mammapps.data.di.ChromecastDeviceTypeQualifier
import com.mamm.mammapps.data.di.DeviceSerialQualifier
import com.mamm.mammapps.data.di.DeviceTypeQualifier
import com.mamm.mammapps.data.di.DrmIVQualifier
import com.mamm.mammapps.data.di.DrmSecretKeyQualifier
import com.mamm.mammapps.data.di.DrmUrlQualifier
import com.mamm.mammapps.data.local.SecurePreferencesManager
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
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val securePreferencesManager: SecurePreferencesManager,
    private val cache: Cache,
    @DeviceSerialQualifier private val deviceSerial: String,
    @DeviceTypeQualifier private val deviceType: String,
    @ChromecastDeviceTypeQualifier private val ccastDeviceType: String,
    @DrmUrlQualifier private val drmUrl: String,
    @DrmIVQualifier private val iV64: ByteArray,
    @DrmSecretKeyQualifier private val secretKey64: ByteArray
) : LocalDataSource {

    override suspend fun saveUserCredentials(username: String, password: String) {
        securePreferencesManager.saveCredentials(username, password)
    }

    override fun setShowBrandedContentMenus(show: Boolean) {
        cache.setShowBrandedContentMenus(show)
    }

    override fun setContentPlayProgress(contentId: String, progress: Long) {
        cache.setContentPlayProgress(contentId, progress)
    }

    override fun setLastTimePinWasCorrect(lastTimePinWasCorrect: ZonedDateTime) {
        cache.setLastTimePinWasCorrect(lastTimePinWasCorrect)
    }

    override fun setHomeContent(homeContent: GetHomeContentResponse) {
        cache.setHomeContent(homeContent)
    }

    override fun setCachedSubgenreList(list: List<Subgenre>) {
        cache.setCachedSubgenreList(list)
    }

    override fun setMoviesContent(moviesContent: GetOtherContentResponse) {
        cache.setMoviesContent(moviesContent)
    }

    override fun setDocumentariesContent(documentariesContent: GetOtherContentResponse) {
        cache.setDocumentariesContent(documentariesContent)
    }

    override fun setSportsContent(sportsContent: GetOtherContentResponse) {
        cache.setSportsContent(sportsContent)
    }

    override fun setKidsContent(kidsContent: GetOtherContentResponse) {
        cache.setKidsContent(kidsContent)
    }

    override fun setAdultsContent(adultsContent: GetBrandedContentResponse) {
        cache.setAdultsContent(adultsContent)
    }

    override fun setWarnerContent(warnerContent: GetBrandedContentResponse) {
        cache.setWarnerContent(warnerContent)
    }

    override fun setAcontraContent(acontraContent: GetBrandedContentResponse) {
        cache.setAcontraContent(acontraContent)
    }

    override fun setAMCContent(amcContent: GetBrandedContentResponse) {
        cache.setAMCContent(amcContent)
    }

    override fun setMyMemories(memories: GetMemoriesResponse) {
        cache.setMyMemories(memories)
    }

    override fun setBookmarks(bookmarks: List<Bookmark>) {
        cache.setBookmarks(bookmarks)
    }

    override fun setMostWatched(mostWatched: List<MostWatchedContent>) {
        cache.setMostWatched(mostWatched)
    }

    override fun setRecommended(recommended: GetRecommendedResponse) {
        cache.setRecommended(recommended)
    }

    override suspend fun getUserCredentials(): Pair<String?, String?> {
        return securePreferencesManager.getCredentials()
    }

    override fun getShowBrandedContentMenus(): Boolean? {
        return cache.getShowBrandedContentMenus()
    }

    override fun getLastTimePinWasCorrect(): ZonedDateTime? {
        return cache.getLastTimePinWasCorrect()
    }

    override fun getDeviceSerial(): String {
        return deviceSerial
    }

    override fun getDeviceType(): String {
        return deviceType
    }

    override fun getChromecastDeviceType(): String {
        return ccastDeviceType
    }

    override fun getDrmBaseUrl(): String {
        return drmUrl
    }

    override fun getDrmiV64(): ByteArray {
        return iV64
    }

    override fun getDrmSecretKey64() : ByteArray {
        return secretKey64
    }

    override fun getApplicationVersion(): String {
        return BuildConfig.VERSION_NAME
    }

    override fun getContentPlayProgress(contentId: String): Long {
        return cache.getContentPlayProgress(contentId)
    }

    override fun getContentProgressFlow(): Flow<Map<String, Long>> {
        return cache.getProgressFlow()
    }

    override fun getHomeContent(): GetHomeContentResponse? {
        return cache.getHomeContent()
    }

    override fun getCachedSubgenreList(): List<Subgenre>? {
        return cache.getCachedSubgenreList()
    }

    override fun getMoviesContent(): GetOtherContentResponse? {
        return cache.getMoviesContent()
    }

    override fun getDocumentariesContent(): GetOtherContentResponse? {
        return cache.getDocumentariesContent()
    }

    override fun getSportsContent(): GetOtherContentResponse? {
        return cache.getSportsContent()
    }

    override fun getKidsContent(): GetOtherContentResponse? {
        return cache.getKidsContent()
    }

    override fun getAdultsContent(): GetBrandedContentResponse? {
        return cache.getAdultsContent()
    }

    override fun getWarnerContent(): GetBrandedContentResponse? {
        return cache.getWarnerContent()
    }

    override fun getAcontraContent(): GetBrandedContentResponse? {
        return cache.getAcontraContent()
    }

    override fun getAMCContent(): GetBrandedContentResponse? {
        return cache.getAMCContent()
    }

    override fun getMyMemories(): GetMemoriesResponse? {
        return cache.getMyMemories()
    }

    override fun getBookmarks(): List<Bookmark>? {
        return cache.getBookmarks()
    }

    override fun getMostWatched(): List<MostWatchedContent>? {
        return cache.getMostWatched()
    }

    override fun getRecommended(): GetRecommendedResponse? {
        return cache.getRecommended()
    }

    override fun clearUserCredentials() {
        securePreferencesManager.clearCredentials()
    }

    override fun clearContentPlayProgress() {
        cache.clearContentPlayProgress()
    }

    override fun clearCache() {
        cache.clear()
    }

}