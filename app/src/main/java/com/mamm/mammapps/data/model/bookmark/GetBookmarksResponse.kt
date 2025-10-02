package com.mamm.mammapps.data.model.bookmark

import com.google.gson.annotations.SerializedName

data class GetBookmarksResponse(
    @SerializedName("bookmarks")
    val bookmarks: List<Bookmark>? = null
)

data class Bookmark(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("shortDesc")
    val shortDesc: String? = null,
    @SerializedName("longDesc")
    val longDesc: String? = null,
    @SerializedName("duration")
    val duration: Int? = null,
    @SerializedName("startDate")
    val startDate: String? = null,
    @SerializedName("expiryDate")
    val expiryDate: String? = null,
    @SerializedName("logoURL")
    val logoURL: String? = null,
    @SerializedName("poster_logo")
    val posterLogo: String? = null,
    @SerializedName("deliveryURL")
    val deliveryURL: String? = null,
    @SerializedName("channelId")
    val channelId: String? = null,
    @SerializedName("currentTime")
    val currentTime: Int? = null,
    @SerializedName("fcStored")
    val fcStored: String? = null,
    @SerializedName("subgenreById")
    val subgenreById: Int? = null,
    @SerializedName("fcIni")
    val fcIni: String? = null,
    @SerializedName("fcEnd")
    val fcEnd: String? = null
)

