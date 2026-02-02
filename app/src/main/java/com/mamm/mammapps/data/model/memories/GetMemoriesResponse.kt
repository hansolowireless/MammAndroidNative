package com.mamm.mammapps.data.model.memories

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetMemoriesResponse(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("videos")
    val videos: List<MemoryItem> = emptyList(),
    @SerializedName("slideshows")
    val slideshows: List<MemoryItem> = emptyList()
)

@Parcelize
data class MemoryItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("duration")
    val duration: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("folder_id")
    val folderId: Int
) : Parcelable