package com.mamm.mammapps.data.datasource.local

import java.time.ZonedDateTime

interface LocalDataSource {

    suspend fun saveUserCredentials(username: String, password: String)
    fun setShowBrandedContentMenus(show: Boolean)
    fun setLastTimePinWasCorrect(lastTimePinWasCorrect: ZonedDateTime)

    suspend fun getUserCredentials(): Pair<String?, String?>
    fun getShowBrandedContentMenus () : Boolean?
    fun getLastTimePinWasCorrect(): ZonedDateTime?
    fun getDeviceSerial(): String
    fun getDeviceType(): String
    fun getChromecastDeviceType(): String
    fun getDrmBaseUrl(): String
    fun getDrmiV64(): ByteArray
    fun getDrmSecretKey64() : ByteArray
}