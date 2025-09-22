package com.mamm.mammapps.data.model.epg

import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.section.EPGEvent

data class EPGChannelContent (
    val channel: Channel,
    val events: List<EPGEvent>
)