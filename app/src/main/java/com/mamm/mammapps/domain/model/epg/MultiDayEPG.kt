package com.mamm.mammapps.domain.model.epg

import java.time.LocalDate

data class MultiDayEPG (
    val multiDayEPG: Map<LocalDate, List<EPGChannelContent>>
)
