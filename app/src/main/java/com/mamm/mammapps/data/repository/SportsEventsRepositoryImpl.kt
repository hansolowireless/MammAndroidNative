package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.remote.SportsCalendarRemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.SportsEventsRepository
import com.mamm.mammapps.domain.model.SportsEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
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

    // Example date format: "20260227210000 +0100"
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss Z")

    override suspend fun getSportsEvents(): List<SportsEvent> {
        return withContext(Dispatchers.IO) {
            try {
                val response = remoteDatasource.getSportsEvents()
                response.programmes?.map { dto ->
                    SportsEvent(
                        title = dto.title ?: "",
                        description = dto.desc ?: "",
                        startTime = parseDate(dto.start),
                        endTime = parseDate(dto.stop),
                        channelId = dto.channel ?: "",
                        images = dto.icons ?: emptyList()
                    )
                } ?: emptyList()
            } catch (e: Exception) {
                logger.error(TAG, "Error mapping football events: ${e.message}")
                emptyList()
            }
        }
    }

    private fun parseDate(dateStr: String?): ZonedDateTime? {
        if (dateStr.isNullOrBlank()) return null
        return try {
            ZonedDateTime.parse(dateStr, dateFormatter)
        } catch (e: DateTimeParseException) {
            logger.error(TAG, "Failed to parse date: $dateStr, error: ${e.message}")
            null
        }
    }
}
