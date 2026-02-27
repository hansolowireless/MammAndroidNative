package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.remote.SportsCalendarRemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.mapper.toDomain
import com.mamm.mammapps.domain.interfaces.SportsEventsRepository
import com.mamm.mammapps.domain.model.SportsEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SportsEventsRepositoryImpl @Inject constructor(
    private val remoteDatasource: SportsCalendarRemoteDatasource,
    private val logger: Logger
) : SportsEventsRepository {

    companion object {
        private const val TAG = "SportsEventsRepositoryImpl"
    }

    override suspend fun getSportsEvents(): Result<List<SportsEvent>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val response = remoteDatasource.getSportsEvents()
                response.programmes?.map { dto -> dto.toDomain() } ?: emptyList()
            }.onFailure { e ->
                logger.error(TAG, "Error mapping football events: ${e.message}")
            }
        }
    }
}
