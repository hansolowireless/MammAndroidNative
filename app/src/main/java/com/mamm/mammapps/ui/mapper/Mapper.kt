package com.mamm.mammapps.ui.mapper

import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.Content
import com.mamm.mammapps.data.model.Event
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentRowUI
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.util.AppConstants.Companion.VERTICAL_ASPECT_RATIO

fun Channel.toContentEntityUI() = ContentEntityUI(
    imageUrl = logoURL?.replace(".png", "_viewer.png")?.replace(".jpg", "_viewer.jpg") ?: "",
    title = "",
    subtitle = ""
)

fun Content.toContentEntityUI() = ContentEntityUI(
    imageUrl = posterURL ?: "",
    title = title ?: "",
    subtitle = shortDesc,
    aspectRatio = VERTICAL_ASPECT_RATIO,
    height = Dimensions.contentEntityHeight
)

fun Event.toContentEntityUI() = ContentEntityUI(
    imageUrl = logoURL ?: "",
    title = title ?: "",
    subtitle = subtitle,
    aspectRatio = VERTICAL_ASPECT_RATIO,
    height = Dimensions.contentEntityHeight
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
                items = items.plus(items).plus(items)
            )
        } else null
    } ?: emptyList()
}

