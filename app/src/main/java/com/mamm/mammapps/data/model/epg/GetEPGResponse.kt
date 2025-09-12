package com.mamm.mammapps.data.model.epg

import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.EPGEvent

data class GetEPGResponse(
    @SerializedName("events")
    val events: List<EPGEvent>? = null
)
