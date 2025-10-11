package com.mamm.mammapps.data.model.recommended

import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.bookmark.Recommended

data class GetRecommendedResponse(
    @SerializedName("vod")
    val vods: List<Recommended>? = null,

    @SerializedName("cutv")
    val cutvs: List<Recommended>? = null
)