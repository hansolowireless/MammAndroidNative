package com.mamm.mammapps.data.datasource.local

import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface LocalDataSource {

    suspend fun saveUserCredentials(username: String, password: String)
    fun setShowBrandedContentMenus(show: Boolean)
    fun setLastTimePinWasCorrect(lastTimePinWasCorrect: ZonedDateTime)
    fun setContentPlayProgress(contentId: String, progress: Long)

    suspend fun getUserCredentials(): Pair<String?, String?>
    fun getShowBrandedContentMenus () : Boolean?
    fun getLastTimePinWasCorrect(): ZonedDateTime?
    fun getDeviceSerial(): String
    fun getDeviceType(): String
    fun getChromecastDeviceType(): String
    fun getDrmBaseUrl(): String
    fun getDrmiV64(): ByteArray
    fun getDrmSecretKey64() : ByteArray
    fun getApplicationVersion(): String
    fun getContentPlayProgress(contentId: String): Long
    fun getContentProgressFlow(): Flow<Map<String, Long>>

    fun clearContentPlayProgress()

}