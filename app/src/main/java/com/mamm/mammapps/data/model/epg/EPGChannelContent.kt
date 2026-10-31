package com.mamm.mammapps.data.model.epg

import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.ChannelDto
import com.mamm.mammapps.data.model.section.EPGEventDto

data class EPGChannelContentDto (
    @SerializedName("channel")
    val channel: ChannelDto,
    @SerializedName("events")
    val events: List<EPGEventDto>
)
