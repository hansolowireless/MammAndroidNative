package com.mamm.mammapps.data.model.sportsevent

import com.google.gson.annotations.SerializedName

data class SportsEventListDto(
    @SerializedName("channels") val channels: List<SportsChannelDto>? = null,
    @SerializedName("programmes") val programmes: List<SportsEventDto>? = null
)

data class SportsChannelDto(
    @SerializedName("id") val id: String?,
    @SerializedName("display-name") val displayName: String?,
    @SerializedName("icon") val icon: String?
)

data class SportsEventDto(
    @SerializedName("start") val start: String?,
    @SerializedName("stop") val stop: String?,
    @SerializedName("channel") val channel: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("desc") val desc: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("icons") val icons: List<String>? = null
)
