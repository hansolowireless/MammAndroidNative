package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.remote.SportsCalendarRemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.SportsEventsRepository
import com.mamm.mammapps.domain.model.SportsEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SportsEventsRepositoryImpl @Inject constructor(
    private val remoteDatasource: SportsCalendarRemoteDatasource,
    private val logger: Logger
) : SportsEventsRepository {

    // Example date format: "20260227210000 +0100"
    private val dateFormat = SimpleDateFormat("yyyyMMddHHmmss Z", Locale.getDefault())

    override suspend fun getFootballEvents(): List<SportsEvent> {
        return withContext(Dispatchers.IO) {
            try {
                val response = remoteDatasource.getFootballEvents()
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
                logger.error("SportsEventsRepositoryImpl", "Error mapping football events: ${e.message}")
                emptyList()
            }
        }
    }

    private fun parseDate(dateStr: String?): Date? {
        if (dateStr.isNullOrBlank()) return null
        return try {
            dateFormat.parse(dateStr)
        } catch (e: ParseException) {
            logger.error("SportsEventsRepositoryImpl", "Failed to parse date: $dateStr, error: ${e.message}")
            null
        }
    }
}
