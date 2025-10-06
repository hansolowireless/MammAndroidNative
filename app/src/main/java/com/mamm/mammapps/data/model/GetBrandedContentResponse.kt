package com.mamm.mammapps.data.model

import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.branded.BrandedVod
import com.mamm.mammapps.data.model.branded.Featured
import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.data.model.section.SectionVod


data class GetBrandedContentResponse(
    @SerializedName("featured")
    val featured: List<Featured>? = null,

    @SerializedName("vods")
    val vods: List<BrandedVod>? = null,

    @SerializedName("events")
    val events: List<EPGEvent>? = null
)



