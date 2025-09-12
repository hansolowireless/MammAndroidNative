package com.mamm.mammapps.data.datasource.local

import com.mamm.mammapps.data.local.SecurePreferencesManager
import com.mamm.mammapps.data.logger.Logger
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val securePreferencesManager: SecurePreferencesManager,
    private val logger: Logger
) : LocalDataSource {

    override suspend fun saveUserCredentials(username: String, password: String) {
        securePreferencesManager.saveCredentials(username, password)
    }

    override suspend fun getUserCredentials(): Pair<String?, String?> {
        return securePreferencesManager.getCredentials()
    }

    fun clearUserCredentials() {
        securePreferencesManager.clearCredentials()
    }
}