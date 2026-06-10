package com.mamm.mammapps.domain.model.epg

import com.mamm.mammapps.domain.model.entity.Channel
import com.mamm.mammapps.domain.model.entity.Event

data class EPGChannelContent (
    val channel: Channel,
    val events: List<Event>
)
