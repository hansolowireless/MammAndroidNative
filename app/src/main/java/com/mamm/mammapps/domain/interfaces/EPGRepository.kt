package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.domain.model.epg.EPGChannelContent
import com.mamm.mammapps.domain.model.entity.Event
import java.time.LocalDate

interface EPGRepository {
    suspend fun getEPG(date: LocalDate) : Result<List<EPGChannelContent>>
    fun getLiveEventForChannel(channelId: Int) : Event?
    fun findContent(channelId: Int, eventId: Int, date: LocalDate) : Event
    fun clearCache()
}