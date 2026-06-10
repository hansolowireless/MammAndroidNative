package com.mamm.mammapps.data.datasource.local

import com.mamm.mammapps.BuildConfig
import com.mamm.mammapps.data.cache.Cache
import com.mamm.mammapps.data.di.ChromecastDeviceTypeQualifier
import com.mamm.mammapps.data.di.DeviceSerialQualifier
import com.mamm.mammapps.data.di.DeviceTypeQualifier
import com.mamm.mammapps.data.di.DrmIVQualifier
import com.mamm.mammapps.data.di.DrmSecretKeyQualifier
import com.mamm.mammapps.data.di.DrmJwtSecretQualifier
import com.mamm.mammapps.data.di.DrmUrlQualifier
import com.mamm.mammapps.data.local.SharedPreferencesManager
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
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val cache: Cache,
    @DeviceSerialQualifier private val deviceSerial: String,
    @DeviceTypeQualifier private val deviceType: String,
    @ChromecastDeviceTypeQualifier private val ccastDeviceType: String,
    @DrmUrlQualifier private val drmUrl: String,
    @DrmIVQualifier private val iV64: ByteArray,
    @DrmSecretKeyQualifier private val secretKey64: ByteArray,
    @DrmJwtSecretQualifier private val jwtSecretKey: ByteArray
) : LocalDataSource {

    override fun setShowBrandedContentMenus(show: Boolean) {
        cache.setShowBrandedContentMenus(show)
    }

    override fun setContentPlayProgress(contentId: String, progress: Long) {
        cache.setContentPlayProgress(contentId, progress)
    }

    override fun setLastTimePinWasCorrect(lastTimePinWasCorrect: ZonedDateTime) {
        cache.setLastTimePinWasCorrect(lastTimePinWasCorrect)
    }

    override fun setHomeContent(homeContent: GetHomeContentResponseDto) {
        cache.setHomeContent(homeContent)
    }

    override fun setCachedSubgenreList(list: List<SubgenreDto>) {
        cache.setCachedSubgenreList(list)
    }

    override fun setMoviesContent(moviesContent: GetOtherContentResponseDto) {
        cache.setMoviesContent(moviesContent)
    }

    override fun setDocumentariesContent(documentariesContent: GetOtherContentResponseDto) {
        cache.setDocumentariesContent(documentariesContent)
    }

    override fun setSportsContent(sportsContent: GetOtherContentResponseDto) {
        cache.setSportsContent(sportsContent)
    }

    override fun setKidsContent(kidsContent: GetOtherContentResponseDto) {
        cache.setKidsContent(kidsContent)
    }

    override fun setAdultsContent(adultsContent: GetBrandedContentResponseDto) {
        cache.setAdultsContent(adultsContent)
    }

    override fun setWarnerContent(warnerContent: GetBrandedContentResponseDto) {
        cache.setWarnerContent(warnerContent)
    }

    override fun setAcontraContent(acontraContent: GetBrandedContentResponseDto) {
        cache.setAcontraContent(acontraContent)
    }

    override fun setAMCContent(amcContent: GetBrandedContentResponseDto) {
        cache.setAMCContent(amcContent)
    }

    override fun setMyMemories(memories: GetMemoriesResponseDto) {
        cache.setMyMemories(memories)
    }

    override fun setBookmarks(bookmarks: List<BookmarkDto>) {
        cache.setBookmarks(bookmarks)
    }

    override fun setMostWatched(mostWatched: List<MostWatchedContentDto>) {
        cache.setMostWatched(mostWatched)
    }

    override fun setRecommended(recommended: GetRecommendedResponseDto) {
        cache.setRecommended(recommended)
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

    override fun getDrmJwtSecretKey() : ByteArray {
        return jwtSecretKey
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

    override fun getHomeContent(): GetHomeContentResponseDto? {
        return cache.getHomeContent()
    }

    override fun getCachedSubgenreList(): List<SubgenreDto>? {
        return cache.getCachedSubgenreList()
    }

    override fun getMoviesContent(): GetOtherContentResponseDto? {
        return cache.getMoviesContent()
    }

    override fun getDocumentariesContent(): GetOtherContentResponseDto? {
        return cache.getDocumentariesContent()
    }

    override fun getSportsContent(): GetOtherContentResponseDto? {
        return cache.getSportsContent()
    }

    override fun getKidsContent(): GetOtherContentResponseDto? {
        return cache.getKidsContent()
    }

    override fun getAdultsContent(): GetBrandedContentResponseDto? {
        return cache.getAdultsContent()
    }

    override fun getWarnerContent(): GetBrandedContentResponseDto? {
        return cache.getWarnerContent()
    }

    override fun getAcontraContent(): GetBrandedContentResponseDto? {
        return cache.getAcontraContent()
    }

    override fun getAMCContent(): GetBrandedContentResponseDto? {
        return cache.getAMCContent()
    }

    override fun getMyMemories(): GetMemoriesResponseDto? {
        return cache.getMyMemories()
    }

    override fun getBookmarks(): List<BookmarkDto>? {
        return cache.getBookmarks()
    }

    override fun getMostWatched(): List<MostWatchedContentDto>? {
        return cache.getMostWatched()
    }

    override fun getRecommended(): GetRecommendedResponseDto? {
        return cache.getRecommended()
    }

    override fun clearContentPlayProgress() {
        cache.clearContentPlayProgress()
    }

    override fun clearCache() {
        cache.clear()
    }

}