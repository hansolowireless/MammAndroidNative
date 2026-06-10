package com.mamm.mammapps.data.model

import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.section.EPGEventDto
import com.mamm.mammapps.data.model.section.SectionVodDto

data class GetOtherContentResponseDto(
    @SerializedName("events")
    val events: List<EPGEventDto>? = null,

    @SerializedName("vods")
    val vods: List<SectionVodDto>? = null
)
