package com.mamm.mammapps.data.model.epg

import java.time.LocalDate

data class MultiDayEPGDto (
    val multiDayEPG: Map<LocalDate, List<EPGChannelContentDto>>
)
