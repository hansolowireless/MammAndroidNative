package com.mamm.mammapps.domain.model.memories

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Memories(
    val userId: Int,
    val updatedAt: String,
    val sections: List<MemorySection>? = null
) : Parcelable

@Parcelize
data class MemorySection(
    val title: String,
    val items: List<MemoryItem> = emptyList()
) : Parcelable

@Parcelize
data class MemoryItem(
    val id: Int,
    val type: String,
    val title: String,
    val thumbnail: String,
    val url: String,
    val duration: String?,
    val createdAt: String,
    val folderId: Int
) : Parcelable
