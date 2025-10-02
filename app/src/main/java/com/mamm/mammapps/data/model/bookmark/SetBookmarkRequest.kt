package com.mamm.mammapps.data.model.bookmark

import com.google.gson.annotations.SerializedName

data class SetBookmarkRequest(
    @SerializedName("id_user")
    val userId: String? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("id")
    val contentId: Int? = null,
    @SerializedName("time")
    val time: Int? = null
)
