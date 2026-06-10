package com.mamm.mammapps.data.model.epg

import com.mamm.mammapps.data.model.ChannelDto
import com.mamm.mammapps.data.model.section.EPGEventDto

data class EPGChannelContentDto (
    val channel: ChannelDto,
    val events: List<EPGEventDto>
)