package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.domain.model.SportsEvent

interface SportsEventsRepository {
    suspend fun getSportsEvents(): List<SportsEvent>
}
