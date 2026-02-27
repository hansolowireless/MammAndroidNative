package com.mamm.mammapps.domain.usecases.content

import com.mamm.mammapps.domain.interfaces.SportsEventsRepository
import com.mamm.mammapps.domain.model.SportsEvent
import java.time.ZonedDateTime
import javax.inject.Inject

class GetSportsCalendarUseCase @Inject constructor(
    private val repository: SportsEventsRepository
) {
    suspend operator fun invoke(): Result<List<SportsEvent>> {
        return repository.getSportsEvents().map { events ->
            val now = ZonedDateTime.now()
            
            // Filter events from today onwards and sort by start time
            events
                .filter { it.endTime?.isAfter(now) == true }
                .sortedBy { it.startTime }
        }
    }
}
