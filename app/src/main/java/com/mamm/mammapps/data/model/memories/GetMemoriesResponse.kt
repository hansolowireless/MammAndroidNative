package com.mamm.mammapps.data.model.memories

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetMemoriesResponseDto(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("sections")
    val sections: List<MemorySectionDto>? = null
) : Parcelable

@Parcelize
data class MemorySectionDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("items")
    val items: List<MemoryItemDto> = emptyList()
) : Parcelable

@Parcelize
data class MemoryItemDto(
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