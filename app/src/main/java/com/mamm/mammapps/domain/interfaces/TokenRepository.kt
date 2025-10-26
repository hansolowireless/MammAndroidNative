package com.mamm.mammapps.domain.interfaces

interface TokenRepository {
    suspend fun storeK1KeyEncrypted(url: String): Result<Unit>
    fun generateJwtToken(
        contentID: String,
        eventType: String,
        chromecast: Boolean = false
    ): String
    fun generateSToken(url: String): Result<String>
    suspend fun refreshIp(): Result<Unit>
}