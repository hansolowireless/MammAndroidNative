package com.mamm.mammapps.data.datasource.local

import java.time.ZonedDateTime

interface LocalDataSource {

    suspend fun saveUserCredentials(username: String, password: String)
    fun setLastTimePinWasCorrect(lastTimePinWasCorrect: ZonedDateTime)

    suspend fun getUserCredentials(): Pair<String?, String?>
    fun getLastTimePinWasCorrect(): ZonedDateTime?

}