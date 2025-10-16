package com.mamm.mammapps.ui.mapper

import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.Event
import com.mamm.mammapps.data.model.Genre
import com.mamm.mammapps.data.model.GetBrandedContentResponse
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.data.model.HomeFeatured
import com.mamm.mammapps.data.model.Serie
import com.mamm.mammapps.data.model.VoD
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.bookmark.Recommended
import com.mamm.mammapps.data.model.branded.BrandedVod
import com.mamm.mammapps.data.model.branded.BrandedFeatured
import com.mamm.mammapps.data.model.mostwatched.MostWatchedContent
import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.data.model.section.SectionVod
import com.mamm.mammapps.data.model.serie.Episode
import com.mamm.mammapps.data.model.serie.GetSeasonInfoResponse
import com.mamm.mammapps.ui.extension.adult
import com.mamm.mammapps.ui.extension.landscape
import com.mamm.mammapps.ui.extension.squared
import com.mamm.mammapps.ui.extension.toBookmarkStartTimeMs
import com.mamm.mammapps.ui.model.ContentEPGUI
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.ContentListUI
import com.mamm.mammapps.ui.model.ContentRowUI
import com.mamm.mammapps.ui.model.CustomizedContent
import com.mamm.mammapps.ui.model.DetailInfoUI
import com.mamm.mammapps.ui.model.SeasonUI
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.model.player.FingerPrintInfoUI
import com.mamm.mammapps.ui.model.player.LiveEventInfoUI
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.Ratios
import com.mamm.mammapps.util.orRandom
import kotlin.math.log

fun Any.toContentEntityUI(isAdult: Boolean = false): ContentEntityUI? {
    return when (this) {
        is Channel -> this.toContentEntityUI()
        is VoD -> this.toContentEntityUI()
        is Event -> this.toContentEntityUI()
        is Serie -> this.toContentEntityUI()
        is EPGEvent -> this.toContentEntityUI(isAdult)
        is SectionVod -> this.toContentEntityUI(isAdult)
        is BrandedVod -> this.toContentEntityUI()
        is BrandedFeatured -> this.toContentEntityUI()
        is Bookmark -> this.toContentEntityUI()
        is MostWatchedContent -> this.toContentEntityUI()
        is Recommended -> this.toContentEntityUI()
        is HomeFeatured -> this.toContentEntityUI()
        else -> null
    }
}

//--------------------region Home------------------------
fun Channel.toContentEntityUI() = ContentEntityUI(
    identifier = ContentIdentifier.Channel(id.orRandom()),
    imageUrl = logoURL?.landscape().orEmpty(),
    horizontalImageUrl = logoURL?.landscape().orEmpty(),
    title = name.orEmpty(),
    detailInfo = DetailInfoUI(squareLogo = logoURL?.squared()),
)

fun VoD.toContentEntityUI() = ContentEntityUI(
    identifier = ContentIdentifier.VoD(id.orRandom()),
    imageUrl = posterURL ?: "",
    horizontalImageUrl = logoURL.orEmpty(),
    title = title ?: "",
    aspectRatio = Ratios.VERTICAL,
    height = Dimensions.contentEntityHeight,
    detailInfo = DetailInfoUI(
        metadata = metadata,
        description = longDesc.orEmpty(),
        subgenreId = this.subgenreById
    )
)

fun Event.toContentEntityUI() = ContentEntityUI(
    identifier = ContentIdentifier.Event(id.orRandom()),
    imageUrl = logoURL.orEmpty(),
    horizontalImageUrl = logoURL.orEmpty(),
    title = title.orEmpty(),
    aspectRatio = Ratios.VERTICAL,
    height = Dimensions.contentEntityHeight,
    detailInfo = DetailInfoUI(
        subtitle = subtitle.orEmpty(),
        description = description.orEmpty(),
        subgenreId = this.subgenreById
    )
)

fun Serie.toContentEntityUI() = ContentEntityUI(
    identifier = ContentIdentifier.Serie(id.orRandom()),
    imageUrl = serieLogoUrl.orEmpty(),
    horizontalImageUrl = serieLogoUrl.orEmpty(),
    title = title.orEmpty(),
    detailInfo = DetailInfoUI(
        description = longDesc.orEmpty()
    ),
    aspectRatio = Ratios.HORIZONTAL,
    height = Dimensions.channelEntityHeight,
)

fun HomeFeatured.toContentEntityUI(): ContentEntityUI? {
    val format = format ?: return null
    val id = id ?: return null

    return ContentEntityUI(
        identifier = ContentIdentifier.fromFeaturedFormat(format = format, id = id, channelById = channelById),
        imageUrl = logoURL.orEmpty(),
        horizontalImageUrl = logoURL.orEmpty(),
        title = title.orEmpty()
    )
}

fun EPGEvent.toContentEntityUI(isAdult: Boolean = false) = ContentEntityUI(
    identifier = ContentIdentifier.Event(getId()),
    imageUrl = (posterLogo?.takeIf { it.isNotBlank() }
        ?: eventLogoUrl500?.takeIf { it.isNotBlank() })
        .orEmpty().adult(isAdult),
    horizontalImageUrl = eventLogoUrl.orEmpty(),
    title = getTitle(),
    aspectRatio = Ratios.VERTICAL,
    height = Dimensions.contentEntityHeight,
    detailInfo = DetailInfoUI(
        metadata = getMetadata(),
        description = getDescription(),
        subgenreId = this.idSubgenre?.toInt()
    )
)

fun SectionVod.toContentEntityUI(isAdult: Boolean = false) = ContentEntityUI(
    identifier = ContentIdentifier.VoD(getId()),
    imageUrl = (posterLogo?.takeIf { it.isNotBlank() }
        ?: eventLogoUrl500?.takeIf { it.isNotBlank() })
        .orEmpty().adult(isAdult),
    horizontalImageUrl = eventLogoTitleUrl.orEmpty(),
    title = getTitle(),
    aspectRatio = Ratios.VERTICAL,
    height = Dimensions.contentEntityHeight,
    detailInfo = DetailInfoUI(
        description = getDescription(),
        metadata = getMetadata(),
        subgenreId = this.idSubgenre?.toInt()
    )
)

fun BrandedVod.toContentEntityUI() = ContentEntityUI(
    identifier = ContentIdentifier.VoD(getId()),
    imageUrl = contentLogo.orEmpty(),
    horizontalImageUrl = contentLogo.orEmpty(),
    title = getTitle(),
    aspectRatio = Ratios.VERTICAL,
    height = Dimensions.contentEntityHeight,
    detailInfo = DetailInfoUI(
        description = getDescription()
    )
)

fun BrandedFeatured.toContentEntityUI(): ContentEntityUI? {
    val format = format ?: return null
    val id = id ?: return null

    return ContentEntityUI(
        identifier = ContentIdentifier.fromFormat(format = format, id = id),
        imageUrl = logoUrl.orEmpty(),
        horizontalImageUrl = logoUrl.orEmpty(),
        title = title.orEmpty(),
        aspectRatio = Ratios.HORIZONTAL,
        height = Dimensions.channelEntityHeight,
        detailInfo = DetailInfoUI(
            description = description.orEmpty()
        )
    )
}

fun Bookmark.toContentEntityUI(): ContentEntityUI? {
    val format = type ?: return null
    val id = id ?: return null
    return ContentEntityUI(
        identifier = ContentIdentifier.fromFormat(format = format, id = id),
        imageUrl = posterLogo.orEmpty(),
        horizontalImageUrl = logoURL.orEmpty(),
        title = title.orEmpty(),
        detailInfo = DetailInfoUI(
            description = longDesc.orEmpty()
        ),
        aspectRatio = Ratios.VERTICAL,
        height = Dimensions.contentEntityHeight,
        customContentType = CustomizedContent.BookmarkType
    )
}

fun MostWatchedContent.toContentEntityUI(): ContentEntityUI {
    return ContentEntityUI(
        identifier = ContentIdentifier.VoD(
            id = id.orRandom()
        ),
        imageUrl = posterLogo.orEmpty(),
        horizontalImageUrl = logoURL.orEmpty(),
        title = title.orEmpty(),
        detailInfo = DetailInfoUI(
            description = longDesc.orEmpty()
        ),
        aspectRatio = Ratios.VERTICAL,
        height = Dimensions.contentEntityHeight,
        customContentType = CustomizedContent.MostWatchedType
    )
}

fun Recommended.toContentEntityUI(): ContentEntityUI? {
    val format = type ?: return null
    val id = id ?: return null
    return ContentEntityUI(
        identifier = ContentIdentifier.fromFormat(format = format, id = id),
        imageUrl = posterLogo.orEmpty(),
        horizontalImageUrl = logoURL.orEmpty(),
        title = title.orEmpty(),
        detailInfo = DetailInfoUI(
            description = longDesc.orEmpty()
        ),
        aspectRatio = Ratios.VERTICAL,
        height = Dimensions.contentEntityHeight,
        customContentType = CustomizedContent.RecommendedType
    )
}

//--------------------endregion Home------------------------


//-------------------------EPG----------------------------
fun Channel.toContentEPGUI() = ContentEPGUI(
    identifier = ContentIdentifier.Channel(id.orRandom()),
    title = name.orEmpty(),
    imageUrl = logoTitleURL.orEmpty()
)

//-------------------------region ContentAsListItem----------------------------
fun EPGEvent.toContentListUI() = ContentListUI(
    identifier = ContentIdentifier.Event(getId()),
    imageUrl = eventLogoUrl500?.takeIf { it.isNotBlank() }
        .orEmpty(),
    title = getTitle(),
    detailInfo = DetailInfoUI(
        description = getDescription()
    )
)

fun Episode.toContentListUI() = ContentListUI(
    identifier = ContentIdentifier.VoD(getId()),
    imageUrl = contentLogo.orEmpty(),
    title = getTitle(),
    detailInfo = DetailInfoUI(
        description = getDescription()
    )
)
//-------------------------endregion ContentAsList-------------------------


//-----------region PLAYBACK-------------------
fun Channel.toContentToPlayUI() = ContentToPlayUI(
    identifier = ContentIdentifier.Channel(id.orRandom()),
    deliveryURL = this.deliveryURL.orEmpty(),
    drmUrl = this.drmUrl,
    title = name.orEmpty(),
    imageUrl = logoURL?.squared().orEmpty(),
    isTimeshift = this.timeshift ?: false,
    fingerprintInfo = FingerPrintInfoUI(
        enabled = this.fingerprint ?: false,
        interval = this.fingerprintFrequency,
        duration = this.fingerprintDuration,
        position = this.fingerprintPosition ?: "random",
        text = this.fingerPrintText.orEmpty()
    ),
    watermarkInfo = this.watermark
)

fun VoD.toContentToPlayUI() = ContentToPlayUI(
    identifier = ContentIdentifier.VoD(id.orRandom()),
    deliveryURL = this.deliveryURL.orEmpty(),
    title = title.orEmpty(),
    imageUrl = posterURL.orEmpty()
)

fun Event.toContentToPlayUI() = ContentToPlayUI(
    identifier = ContentIdentifier.Event(id.orRandom()),
    deliveryURL = this.deliveryURL.orEmpty(),
    title = title.orEmpty(),
    imageUrl = logoURL.orEmpty(),
    //It's used to get the start and end dates in order to build the catchup URL
    epgEventInfo = this.toLiveEventInfoUI()
)

fun EPGEvent.toContentToPlayUI() = ContentToPlayUI(
    identifier = ContentIdentifier.Event(getId()),
    deliveryURL = this.deliveryUrl.orEmpty(),
    title = this.getTitle(),
    imageUrl = this.eventLogoUrl500.orEmpty(),
    epgEventInfo = this.toLiveEventInfoUI()
)

fun SectionVod.toContentToPlayUI() = ContentToPlayUI(
    identifier = ContentIdentifier.VoD(getId()),
    deliveryURL = this.deliveryURL.orEmpty(),
    title = this.getTitle(),
    imageUrl = this.posterLogo.orEmpty(),
)

fun BrandedVod.toContentToPlayUI() = ContentToPlayUI(
    identifier = ContentIdentifier.VoD(getId()),
    deliveryURL = this.path.orEmpty(),
    title = this.getTitle(),
    imageUrl = this.contentLogo.orEmpty(),
)

fun BrandedFeatured.toContentToPlayUI(): ContentToPlayUI? {
    val format = format ?: return null
    val id = id ?: return null

    return ContentToPlayUI(
        identifier = ContentIdentifier.fromFormat(format = format, id = id),
        deliveryURL = this.deliveryUrl.orEmpty(),
        title = this.title.orEmpty(),
        imageUrl = this.logoUrl.orEmpty(),
    )
}

fun Episode.toContentToPlayUI() = ContentToPlayUI(
    identifier = ContentIdentifier.VoD(getId()),
    deliveryURL = this.path.orEmpty(),
    title = this.getTitle(),
    imageUrl = this.contentLogo.orEmpty(),
)

fun Bookmark.toContentToPlayUI(): ContentToPlayUI? {
    val format = type ?: return null
    val id = id ?: return null
    return ContentToPlayUI(
        identifier = ContentIdentifier.fromFormat(format = format, id = id),
        deliveryURL = this.deliveryURL.orEmpty(),
        title = this.title.orEmpty(),
        imageUrl = this.logoURL.orEmpty(),
        initialPlayPositionMs = this.currentTime.toBookmarkStartTimeMs(),
        epgEventInfo = LiveEventInfoUI(
            title = this.title.orEmpty(),
            eventStart = this.startDateTime,
            eventEnd = this.endDateTime
        )
    )
}

fun MostWatchedContent.toContentToPlayUI() = ContentToPlayUI(
    identifier = ContentIdentifier.VoD(id.orRandom()),
    deliveryURL = this.deliveryURL.orEmpty(),
    title = this.title.orEmpty(),
    imageUrl = this.logoURL.orEmpty(),
)

fun Recommended.toContentToPlayUI() = ContentToPlayUI(
    identifier = ContentIdentifier.VoD(id.orRandom()),
    deliveryURL = this.deliveryURL.orEmpty(),
    title = this.title.orEmpty(),
    imageUrl = this.logoURL.orEmpty(),
)


//------------------------LIVE EVENT INFO------------------------
fun EPGEvent.toLiveEventInfoUI(): LiveEventInfoUI = LiveEventInfoUI(
    title = this.getTitle(),
    deliveryURL = this.deliveryUrl.orEmpty(),
    logoURL = this.eventLogoUrl500.orEmpty(),
    eventStart = this.startDateTime,
    eventEnd = this.endDateTime
)

fun Event.toLiveEventInfoUI(): LiveEventInfoUI = LiveEventInfoUI(
    title = this.title.orEmpty(),
    logoURL = this.logoURL.orEmpty(),
    eventStart = this.startDateTime,
    eventEnd = this.endDateTime
)
//----------endregion PLAYBACK----------------------

fun List<ContentEntityUI>.repeat(threshold: Int): List<ContentEntityUI> {
    return if (this.size > threshold) {
        this.plus(this).plus(this)
    } else {
        this
    }
}

fun GetHomeContentResponse.toContentUIRows(): List<ContentRowUI> {
    val orderedCategories = categories?.sortedBy { it.pos }
    val rowsWithoutFeatured =  orderedCategories?.mapNotNull { category ->
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
                items = items.repeat(threshold = 5)
            )
        } else null
    } ?: emptyList()

    return this.featured?.let {
        listOf(
            ContentRowUI(
                categoryName = "Eventos Destacados",
                items = it.mapNotNull { featured -> featured.toContentEntityUI() },
                isFeatured = true
            )
        ) + rowsWithoutFeatured
    } ?: rowsWithoutFeatured
}

fun GetOtherContentResponse.toContentUIRows(
    genre: Genre
): List<ContentRowUI> {
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

fun GetBrandedContentResponse.toContentUIRows(
    genre: Genre,
    isAdult: Boolean = false
): List<ContentRowUI> {
    val rows = mutableListOf<ContentRowUI>()

    // Add "Eventos Destacados" row without subgenre filtering
    val allFeaturedItems = featured.orEmpty().mapNotNull { it.toContentEntityUI() }
    if (allFeaturedItems.isNotEmpty()) {
        rows.add(
            ContentRowUI(
                categoryName = "Eventos Destacados",
                items = allFeaturedItems,
                isFeatured = true
            )
        )
    }

    genre.subgenres?.forEach { sub ->
        val subVods = vods.orEmpty().filter { it.idSubgenre == sub.id.toString() }

        // Convertimos a ContentEntityUI
        val items = subVods.mapNotNull { it.toContentEntityUI(isAdult = isAdult) }

        if (items.isNotEmpty()) {
            rows.add(
                ContentRowUI(
                    categoryName = sub.ds.orEmpty(),
                    items = items
                )
            )
        }
    }
    return rows
}

fun List<ContentRowUI>.insertBookmarks(
    bookmarks: List<Bookmark>
): List<ContentRowUI> {
    ContentRowUI(
        categoryName = "Seguir viendo",
        items = bookmarks.mapNotNull { it.toContentEntityUI() }
    ).let {
        return listOf(it) + this
    }
}

fun List<ContentRowUI>.insertRecommended(
    bookmarks: List<Recommended>
): List<ContentRowUI> {
    ContentRowUI(
        categoryName = "Recomendado para ti",
        items = bookmarks.mapNotNull { it.toContentEntityUI() }
    ).let {
        return listOf(it) + this
    }
}


fun List<ContentRowUI>.insertMostWatched(mostWatched: List<MostWatchedContent>): List<ContentRowUI> {
    ContentRowUI(
        categoryName = "Más visto",
        items = mostWatched.map { it.toContentEntityUI() }
    ).let {
        return listOf(it) + this
    }
}

fun List<ContentRowUI>.insertChannelRow(recommended: List<Channel>?): List<ContentRowUI> {
    recommended?.let {
        ContentRowUI(
            categoryName = "Canales",
            items = it.map { channel -> channel.toContentEntityUI() }
        ).let { row ->
            return listOf(row) + this
        }
    }
    return this
}

//----------------region SERIE DETAIL---------------------
fun List<Serie>.toContentUIRows(genre: Genre): List<ContentRowUI> {
    val rows = mutableListOf<ContentRowUI>()
    genre.subgenres?.forEach { sub ->
        val subSeries = this.filter { it.subgenreById == sub.id }
        val items = subSeries.map { it.toContentEntityUI() }
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

fun GetSeasonInfoResponse.toSeasonUIList(): List<SeasonUI> {
    val list = this.tbSeasons?.map { tbSeason ->

        val episodes: List<ContentListUI> =
            tbSeason.tbContentSeasons?.mapNotNull { tbContentSeason ->
                tbContentSeason.contentDetails?.toContentListUI()
            } ?: emptyList()

        SeasonUI(
            order = tbSeason.getOrder(),
            title = tbSeason.getTitle(),
            imageUrl = tbSeason.seasonLogoTitleUrl.orEmpty(),
            episodes = episodes
        )

    }

    return list.orEmpty()
}
//----------------endregion SERIE DETAIL---------------------

//----------------region SIMILAR CONTENT---------------------
fun List<Recommended>.toSimilarContentRow(): ContentRowUI {
    ContentRowUI(
        categoryName = "Contenido Similar",
        items = this.mapNotNull { it.toContentEntityUI() }
    ).let {
        return it
    }
}