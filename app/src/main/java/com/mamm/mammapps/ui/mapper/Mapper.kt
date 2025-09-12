package com.mamm.mammapps.ui.mapper

import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.EPGEvent
import com.mamm.mammapps.data.model.Event
import com.mamm.mammapps.data.model.Genre
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.data.model.SectionVod
import com.mamm.mammapps.data.model.Serie
import com.mamm.mammapps.data.model.VoD
import com.mamm.mammapps.ui.extension.squared
import com.mamm.mammapps.ui.model.ContentDetailUI
import com.mamm.mammapps.ui.model.ContentEPGUI
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.ContentRowUI
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.util.AppConstants.Companion.VERTICAL_ASPECT_RATIO

//Home
fun Channel.toContentEntityUI() = ContentEntityUI(
    imageUrl = logoURL?.replace(".png", "_viewer.png")?.replace(".jpg", "_viewer.jpg") ?: "",
    title = name ?: "",
    subtitle = "",
    identifier = ContentIdentifier.Channel(id.toString())
)

fun VoD.toContentEntityUI() = ContentEntityUI(
    imageUrl = posterURL ?: "",
    title = title ?: "",
    subtitle = shortDesc,
    aspectRatio = VERTICAL_ASPECT_RATIO,
    height = Dimensions.contentEntityHeight,
    identifier = ContentIdentifier.VoD(id.toString())
)

fun Event.toContentEntityUI() = ContentEntityUI(
    imageUrl = logoURL ?: "",
    title = title ?: "",
    subtitle = subtitle,
    aspectRatio = VERTICAL_ASPECT_RATIO,
    height = Dimensions.contentEntityHeight,
    identifier = ContentIdentifier.Event(id.toString())
)

fun Serie.toContentEntityUI () = ContentEntityUI(
    imageUrl = serieLogoUrl ?: "",
    title = title ?: "",
    subtitle = shortDesc,
    aspectRatio = VERTICAL_ASPECT_RATIO,
    height = Dimensions.contentEntityHeight,
    identifier = ContentIdentifier.Serie(id.toString())
)

fun EPGEvent.toContentEntityUI() = ContentEntityUI(
    imageUrl = posterLogo ?: "",
    title = tbEventLanguages?.firstOrNull()?.title ?: "",
    subtitle = tbEventLanguages?.firstOrNull()?.description,
    aspectRatio = VERTICAL_ASPECT_RATIO,
    height = Dimensions.contentEntityHeight,
    identifier = ContentIdentifier.Event(
        tbEventItems?.firstOrNull()?.idEvent.toString() ?: "0"
    )
)

fun SectionVod.toContentEntityUI() = ContentEntityUI(
    imageUrl = posterLogo ?: "",
    title = tbEventLanguages?.firstOrNull()?.title ?: "",
    subtitle = tbEventLanguages?.firstOrNull()?.description,
    aspectRatio = VERTICAL_ASPECT_RATIO,
    height = Dimensions.contentEntityHeight ,
    identifier = ContentIdentifier.VoD(
        tbEventItems?.firstOrNull()?.idContent.toString() ?: "0"
    )
)

//Details
fun VoD.toContentDetailUI() = ContentDetailUI(
    identifier = ContentIdentifier.VoD(id.toString()),
    title = title?: "",
    imageUrl = logoURL?: "",
    description = longDesc,
    metadata = metadata
)

fun Event.toContentDetailUI() = ContentDetailUI(
    identifier = ContentIdentifier.Event(id.toString()),
    title = title?: "",
    imageUrl = logoURL?: "",
    description = description,
)

fun EPGEvent.toContentDetailUI() = ContentDetailUI(
    identifier = ContentIdentifier.Event(
        tbEventItems?.firstOrNull()?.idEvent.toString() ?: "0"
    ),
    title = tbEventLanguages?.firstOrNull()?.title ?: "",
    imageUrl = eventLogoUrl ?: "",
    description = tbEventLanguages?.firstOrNull()?.description,
    metadata = getMetadata()
)

fun SectionVod.toContentDetailUI() = ContentDetailUI(
    identifier = ContentIdentifier.VoD(
        tbEventItems?.firstOrNull()?.idContent.toString() ?: "0"
    ),
    title = tbEventLanguages?.firstOrNull()?.title ?: "",
    imageUrl = eventLogoUrl ?: "",
    description = tbEventLanguages?.firstOrNull()?.description,
    metadata = getMetadata()
)

//EPG
fun Channel.toContentEPGUI() = ContentEPGUI(
    identifier = ContentIdentifier.Channel(id.toString()),
    title = name?: "",
    imageUrl = logoURL?.squared() ?: ""
)

fun GetHomeContentResponse.toContentUIRows(): List<ContentRowUI> {
    return categories?.mapNotNull { category ->
        val items = category.order?.mapNotNull { orderItem ->
            when (orderItem.type) {
                "channel" -> channels?.find { it.id == orderItem.id }?.toContentEntityUI()
                "content" -> contents?.find { it.id == orderItem.id }?.toContentEntityUI()
                "event" -> events?.find { it.id == orderItem.id }?.toContentEntityUI()
                "serie" -> series?.find { it.id == orderItem.id }?.toContentEntityUI()
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

fun GetOtherContentResponse.toContentUIRows(genre: Genre): List<ContentRowUI> {
    val rows = mutableListOf<ContentRowUI>()

    genre.subgenres?.forEach { sub ->
        // Filtramos los events de este subgénero (si aplica)
        val subEvents = events.orEmpty().filter { it.idSubgenre == sub.id.toString() }
        val subVods = vods.orEmpty().filter { it.idSubgenre == sub.id.toString() }

        // Convertimos a ContentEntityUI
        val items = subEvents.map { it.toContentEntityUI() } +
                subVods.map { it.toContentEntityUI() }

        if (items.isNotEmpty()) {
            rows.add(
                ContentRowUI(
                    categoryName = sub.ds ?: "",
                    items = items
                )
            )
        }
    }
    return rows
}



