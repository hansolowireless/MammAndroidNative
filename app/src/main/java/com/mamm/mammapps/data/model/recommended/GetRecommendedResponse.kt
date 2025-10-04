package com.mamm.mammapps.data.model.recommended

import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.bookmark.Bookmark

data class GetRecommendedResponse(
    @SerializedName("vod")
    val vods: List<Bookmark>? = null,

    @SerializedName("cutv")
    val cutvs: List<Bookmark>? = null
)