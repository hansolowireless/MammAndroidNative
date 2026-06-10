package com.mamm.mammapps.data.mapper

import com.mamm.mammapps.data.model.memories.GetMemoriesResponseDto
import com.mamm.mammapps.data.model.memories.MemoryItemDto
import com.mamm.mammapps.data.model.memories.MemorySectionDto
import com.mamm.mammapps.domain.model.memories.Memories
import com.mamm.mammapps.domain.model.memories.MemoryItem
import com.mamm.mammapps.domain.model.memories.MemorySection

fun MemoryItemDto.toDomain(): MemoryItem {
    return MemoryItem(
        id = this.id,
        type = this.type,
        title = this.title,
        thumbnail = this.thumbnail,
        url = this.url,
        duration = this.duration,
        createdAt = this.createdAt,
        folderId = this.folderId
    )
}

fun MemorySectionDto.toDomain(): MemorySection {
    return MemorySection(
        title = this.title,
        items = this.items.map { it.toDomain() }
    )
}

fun GetMemoriesResponseDto.toDomain(): Memories {
    return Memories(
        userId = this.userId,
        updatedAt = this.updatedAt,
        sections = this.sections?.map { it.toDomain() }
    )
}
