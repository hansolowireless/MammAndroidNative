package com.mamm.mammapps.domain.interfaces

interface PlaybackRepository {
    suspend fun getVideoUrlFromCLM(deliveryURL: String) : Result<String>
}