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

    fun clearUserCredentials() {
        securePreferencesManager.clearCredentials()
    }

    override fun clearContentPlayProgress() {
        cache.clearContentPlayProgress()
    }

}