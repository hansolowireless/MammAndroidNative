package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.data.model.epg.EPGChannelContent
import java.time.LocalDate

interface EPGRepository {
    suspend fun getEPG(date: LocalDate) : Result<List<EPGChannelContent>>
    fun getLiveEventForChannel(channelId: Int) : EPGEvent?
}