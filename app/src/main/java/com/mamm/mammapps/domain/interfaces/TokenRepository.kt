package com.mamm.mammapps.domain.interfaces

import com.example.openstream_flutter_rw.data.model.STokenData
import com.mamm.mammapps.data.model.player.JwTokenData

interface TokenRepository {
    suspend fun storeK1KeyEncrypted(url: String): Result<Unit>
    fun generateJwtToken(contentID: String, eventType: String): String
    fun generateSToken(url: String): Result<String>
    suspend fun refreshIp(): Result<Unit>
}