package com.mamm.mammapps.data.model

import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.data.model.section.SectionVod


data class GetOtherContentResponse(
    @SerializedName("events")
    val events: List<EPGEvent>? = null,

    @SerializedName("vods")
    val vods: List<SectionVod>? = null
)



