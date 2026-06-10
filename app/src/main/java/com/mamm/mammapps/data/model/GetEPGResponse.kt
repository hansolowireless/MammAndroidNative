package com.mamm.mammapps.data.model

import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.section.EPGEventDto

data class GetEPGResponseDto(
    @SerializedName("events")
    val events: List<EPGEventDto>? = null
)
