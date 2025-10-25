package com.mamm.mammapps.data.model

import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.branded.BrandedFeatured
import com.mamm.mammapps.data.model.branded.BrandedVod
import com.mamm.mammapps.data.model.section.EPGEvent


data class GetBrandedContentResponse(
    @SerializedName("featured")
    val featured: List<BrandedFeatured>? = null,

    @SerializedName("channels")
    val channels: List<Channel>? = null,

    @SerializedName("vods")
    val vods: List<BrandedVod>? = null,

    @SerializedName("events")
    val events: List<EPGEvent>? = null
)



