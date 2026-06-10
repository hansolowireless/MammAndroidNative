package com.mamm.mammapps.data.model.recommended

import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.bookmark.RecommendedDto

data class GetRecommendedResponseDto(
    @SerializedName("vod")
    val vods: List<RecommendedDto>? = null,

    @SerializedName("cutv")
    val cutvs: List<RecommendedDto>? = null
)