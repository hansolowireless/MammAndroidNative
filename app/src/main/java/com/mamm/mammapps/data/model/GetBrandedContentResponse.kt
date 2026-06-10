package com.mamm.mammapps.data.model

import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.branded.BrandedFeaturedDto
import com.mamm.mammapps.data.model.branded.BrandedVodDto
import com.mamm.mammapps.data.model.section.EPGEventDto

data class GetBrandedContentResponseDto(
    @SerializedName("featured")
    val featured: List<BrandedFeaturedDto>? = null,

    @SerializedName("channels")
    val channels: List<ChannelDto>? = null,

    @SerializedName("vods")
    val vods: List<BrandedVodDto>? = null,

    @SerializedName("events")
    val events: List<EPGEventDto>? = null,

    @SerializedName("series")
    val series: List<SerieDto>? = null
)
