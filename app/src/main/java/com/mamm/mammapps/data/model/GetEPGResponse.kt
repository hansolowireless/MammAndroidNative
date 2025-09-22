package com.mamm.mammapps.data.model

import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.section.EPGEvent

data class GetEPGResponse(
    @SerializedName("events")
    val events: List<EPGEvent>? = null
)
