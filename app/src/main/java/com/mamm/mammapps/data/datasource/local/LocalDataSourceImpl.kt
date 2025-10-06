package com.mamm.mammapps.data.datasource.local

import com.mamm.mammapps.data.cache.Cache
import com.mamm.mammapps.data.local.SecurePreferencesManager
import com.mamm.mammapps.data.logger.Logger
import java.time.ZonedDateTime
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val securePreferencesManager: SecurePreferencesManager,
    private val cache: Cache,
    private val logger: Logger
) : LocalDataSource {

    override suspend fun saveUserCredentials(username: String, password: String) {
        securePreferencesManager.saveCredentials(username, password)
    }

    override fun setLastTimePinWasCorrect(lastTimePinWasCorrect: ZonedDateTime) {
        cache.setLastTimePinWasCorrect(lastTimePinWasCorrect)
    }

    override suspend fun getUserCredentials(): Pair<String?, String?> {
        return securePreferencesManager.getCredentials()
    }

    override fun getLastTimePinWasCorrect(): ZonedDateTime? {
        return cache.getLastTimePinWasCorrect()
    }

    fun clearUserCredentials() {
        securePreferencesManager.clearCredentials()
    }
}