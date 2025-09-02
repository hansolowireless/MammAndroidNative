package com.mamm.mammapps.ui.mapper

import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.Content
import com.mamm.mammapps.data.model.Event
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentRowUI

fun Channel.toContentEntityUI() = ContentEntityUI(
    imageUrl = logoURL ?: "",
    title = name ?: "",
    subtitle = description
)

fun Content.toContentEntityUI() = ContentEntityUI(
    imageUrl = logoURL ?: "",
    title = title ?: "",
    subtitle = shortDesc
)

fun Event.toContentEntityUI() = ContentEntityUI(
    imageUrl = logoURL ?: "",
    title = title ?: "",
    subtitle = subtitle
)

fun GetHomeContentResponse.toContentRows(): List<ContentRowUI> {
    return categories?.mapNotNull { category ->
        val items = category.order?.mapNotNull { orderItem ->
            when (orderItem.type) {
                "channel" -> channels?.find { it.id == orderItem.id }?.toContentEntityUI()
                "content" -> contents?.find { it.id == orderItem.id }?.toContentEntityUI()
                "event" -> events?.find { it.id == orderItem.id }?.toContentEntityUI()
                else -> null
            }
        } ?: emptyList()

        if (items.isNotEmpty()) {
            ContentRowUI(
                categoryName = category.name ?: "",
                items = items
            )
        } else null
    } ?: emptyList()
}

