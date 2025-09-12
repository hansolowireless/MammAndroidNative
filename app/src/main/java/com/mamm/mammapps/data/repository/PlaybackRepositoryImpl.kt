package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import javax.inject.Inject

class PlaybackRepositoryImpl @Inject constructor (
    private val remoteDatasource: RemoteDatasource,
    private val logger: Logger
) : PlaybackRepository {

    override suspend fun getVideoUrlFromCLM(deliveryURL: String): Result<String> {
        return runCatching {
            val locationUrl = remoteDatasource.getUrlFromCLM(deliveryURL)
            locationUrl ?: throw Exception("Location header not found in response")
        }
    }

}