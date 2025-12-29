package com.mamm.mammapps.ui.mapper

import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.Event
import com.mamm.mammapps.data.model.Genre
import com.mamm.mammapps.data.model.GetBrandedContentResponse
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.data.model.HomeFeatured
import com.mamm.mammapps.data.model.Serie
import com.mamm.mammapps.data.model.Subgenre
import com.mamm.mammapps.data.model.VoD
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.bookmark.Recommended
import com.mamm.mammapps.data.model.branded.BrandedFeatured
import com.mamm.mammapps.data.model.branded.BrandedVod
import com.mamm.mammapps.data.model.mostwatched.MostWatchedContent
import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.data.model.section.SectionVod
import com.mamm.mammapps.data.model.serie.Episode
import com.mamm.mammapps.data.model.serie.GetSeasonInfoResponse
import com.mamm.mammapps.data.model.serie.TbContentSeason
import com.mamm.mammapps.ui.constant.UIConstant
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
import com.mamm.mammapps.util.getRandomHashCode
import com.mamm.mammapps.util.orRandom
import java.time.LocalDate

fun Any.toContentEntityUI(): ContentEntityUI? {
    return when (this) {
        is Channel -> this.toContentEntityUI()
        is VoD -> this.toContentEntityUI()
        is Event -> this.toContentEntityUI()
        is Serie -> this.toContentEntityUI()
        is EPGEvent -> this.toContentEntityUI()
        is SectionVod -> this.toContentEntityUI()
        is BrandedVod -> this.toContentEntityUI()
        is BrandedFeatured -> this.toContentEntityUI()
        is Bookmark -> this.toContentEntityUI()
        is MostWatchedContent -> this.toContentEntityUI()
        is Recommended -> this.toContentEntityUI()
        is HomeFeatured -> this.toContentEntityUI()
        else -> null
    }
}

fun Any.toContentToPlayUI(): ContentToPlayUI? {
    return when (this) {
        is Channel -> this.toContentToPlayUI()
        is VoD -> this.toContentToPlayUI()
        is Event -> this.toContentToPlayUI()
        is HomeFeatured -> this.toContentToPlayUI()
        is EPGEvent -> this.toContentToPlayUI()
        is SectionVod -> this.toContentToPlayUI()
        is BrandedVod -> this.toContentToPlayUI()
        is BrandedFeatured -> this.toContentToPlayUI()
        is TbContentSeason -> this.contentDetails?.toContentToPlayUI()
        is Bookmark -> this.toContentToPlayUI()
        is MostWatchedContent -> this.toContentToPlayUI()
        is Recommended -> this.toContentToPlayUI()
        else -> null
    }
}

//--------------------region Home------------------------
fun Channel.toContentEntityUI() = ContentEntityUI(
    identifier = ContentIdentifier.Channel(id.orRandom()),
    imageUrl = logoURL?.landscape().orEmpty(),
    horizontalImageUrl = logoURL?.landscape().orEmpty(),
    title = name.orEmpty(),
    detailInfo = DetailInfoUI(
        squareLogo = logoURL?.squared(),
        description = description.orEmpty()
    ),
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
    val imageUrl = logoTransitions?.first()?.url ?: logoURL.orEmpty()

    return ContentEntityUI(
        identifier = ContentIdentifier.fromFeaturedFormat(
            format = format,
            id = id,
            channelById = channelById
        ),
        imageUrl = imageUrl,
        horizontalImageUrl = logoURL.orEmpty(),
        title = title.orEmpty(),
        isFeatured = true,
        detailInfo = DetailInfoUI(
            description = description.orEmpty()
        )
    )
}

fun EPGEvent.toContentEntityUI() = ContentEntityUI(
    identifier = ContentIdentifier.Event(getId()),
    imageUrl = (posterLogo?.takeIf { it.isNotBlank() }
        ?: eventLogoUrl500?.takeIf { it.isNotBlank() })
        .orEmpty(),
    horizontalImageUrl = eventLogoUrl.orEmpty(),
    title = getTitle(),
    aspectRatio = Ratios.VERTICAL,
    height = Dimensions.contentEntityHeight,
    liveEventInfo = this.toLiveEventInfoUI(),
    detailInfo = DetailInfoUI(
        metadata = getMetadata(),
        description = getDescription(),
        subgenreId = this.idSubgenre?.toInt(),
        channelId = this.getChannelId()
    )
)

fun SectionVod.toContentEntityUI() = ContentEntityUI(
    identifier = ContentIdentifier.VoD(getId()),
    imageUrl = (posterURL?.takeIf { it.isNotBlank() }
        ?: logoURL?.takeIf { it.isNotBlank() })
        .orEmpty(),
    horizontalImageUrl = logoURL.orEmpty(),
    title = title.orEmpty(),
    aspectRatio = Ratios.VERTICAL,
    height = Dimensions.contentEntityHeight,
    detailInfo = DetailInfoUI(
        description = getDescription(),
        metadata = getMetadata(),
        subgenreId = this.subgenreById
    )
)

fun BrandedVod.toContentEntityUI() = ContentEntityUI(
    identifier = ContentIdentifier.VoD(getId()),
    imageUrl = posterLogo.orEmpty(),
    horizontalImageUrl = contentLogo.orEmpty(),
    title = getTitle(),
    aspectRatio = Ratios.VERTICAL,
    height = Dimensions.contentEntityHeight,
    detailInfo = DetailInfoUI(
        description = getDescription(),
        metadata = getMetadata()
    )
)

fun BrandedFeatured.toContentEntityUI(): ContentEntityUI? {
    val format = format ?: return null
    //A veces viene un featured sin formatid porque es solamente un mensaje
    val id = formatId?.toIntOrNull().orRandom()
    val imageUrl = logoTransitions?.first()?.url ?: logoUrl.orEmpty()

    return ContentEntityUI(
        identifier = ContentIdentifier.fromFeaturedFormat(format = format, id = id),
        imageUrl = imageUrl,
        horizontalImageUrl = logoUrl.orEmpty(),
        title = title.orEmpty(),
        isFeatured = true,
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
            description = longDesc.orEmpty(),
            metadata = getMetadata()
        ),
        aspectRatio = Ratios.VERTICAL,
        height = Dimensions.contentEntityHeight,
        customContentType = CustomizedContent.BookmarkType
    )
}

fun MostWatchedContent.toContentEntityUI(): ContentEntityUI? {
    val format = type ?: return null
    val id = id ?: return null
    return ContentEntityUI(
        identifier = ContentIdentifier.fromFormat(
            format = format,
            id = id
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
            description = longDesc.orEmpty(),
            metadata = getMetadata()
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
    title = title.orEmpty(),
    imageUrl = this.posterURL.orEmpty()
)

fun BrandedVod.toContentToPlayUI() = ContentToPlayUI(
    identifier = ContentIdentifier.VoD(getId()),
    deliveryURL = this.path.orEmpty(),
    title = this.getTitle(),
    imageUrl = this.contentLogo.orEmpty()
)

fun HomeFeatured.toContentToPlayUI(): ContentToPlayUI? {
    val format = format ?: return null
    val id = id ?: return null

    return ContentToPlayUI(
        identifier = ContentIdentifier.fromFeaturedFormat(format = format, id = id),
        deliveryURL = this.deliveryURL.orEmpty(),
        title = this.title.orEmpty(),
        imageUrl = this.logoURL.orEmpty(),
    )
}

fun BrandedFeatured.toContentToPlayUI(): ContentToPlayUI? {
    val format = format ?: return null
    val id = formatId?.toIntOrNull() ?: return null

    return ContentToPlayUI(
        identifier = ContentIdentifier.fromFeaturedFormat(format = format, id = id),
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

fun MostWatchedContent.toContentToPlayUI(): ContentToPlayUI? {
    val format = type ?: return null
    val id = id ?: return null
    return ContentToPlayUI(
        identifier = ContentIdentifier.fromFormat(
            format = format,
            id = id
        ),
        deliveryURL = this.deliveryURL.orEmpty(),
        title = this.title.orEmpty(),
        imageUrl = this.logoURL.orEmpty(),
        epgEventInfo = LiveEventInfoUI(
            title = this.title.orEmpty(),
            eventStart = this.startDateTime,
            eventEnd = this.endDateTime
        )
    )
}

fun Recommended.toContentToPlayUI(): ContentToPlayUI? {
    val format = type ?: return null
    val id = id ?: return null
    return ContentToPlayUI(
        identifier = ContentIdentifier.fromFormat(
            format = format,
            id = id
        ),
        deliveryURL = this.deliveryURL.orEmpty(),
        title = this.title.orEmpty(),
        imageUrl = this.logoURL.orEmpty(),
        epgEventInfo = LiveEventInfoUI(
            title = this.title.orEmpty(),
            eventStart = this.startDateTime,
            eventEnd = this.endDateTime
        )
    )
}


//------------------------LIVE EVENT INFO------------------------
fun EPGEvent.toLiveEventInfoUI(): LiveEventInfoUI = LiveEventInfoUI(
    title = this.getTitle(),
    deliveryURL = this.deliveryUrl.orEmpty(),
    logoURL = this.eventLogoUrl500.orEmpty(),
    eventStart = this.startDateTime,
    eventEnd = this.endDateTime,
    fatherChannelId = this.idChannel?.toIntOrNull()
)

fun Event.toLiveEventInfoUI(): LiveEventInfoUI = LiveEventInfoUI(
    title = this.title.orEmpty(),
    logoURL = this.logoURL.orEmpty(),
    eventStart = this.startDateTime,
    eventEnd = this.endDateTime
)
//----------endregion PLAYBACK----------------------

fun GetHomeContentResponse.toContentUIRows(): List<ContentRowUI> {
    val orderedCategories = categories?.sortedBy { it.pos }
    val rowsWithoutFeatured = orderedCategories?.mapNotNull { category ->
        val items = category.order?.mapNotNull { orderItem ->
            when (orderItem.type) {
                "channel" -> channels?.find { it.id == orderItem.id }?.toContentEntityUI()
                "content" -> contents?.find { it.id == orderItem.id }?.toContentEntityUI()
                "event" -> events?.find { it.id == orderItem.id }?.toContentEntityUI()
                "serie" -> series?.find { it.id == orderItem.id }?.toContentEntityUI()
                else -> null
            }
        } ?: emptyList()

        val loadMore = category.loadMore

        if (items.isNotEmpty()) {
            ContentRowUI(
                categoryId = category.id ?: 0,
                categoryName = category.name.orEmpty(),
                items = items,
                loadMore = loadMore
            )
        } else null
    } ?: emptyList()

    return rowsWithoutFeatured
}

fun GetOtherContentResponse.toContentUIRows(
    subgenres: List<Subgenre>
): List<ContentRowUI> {
    val rowsMap = mutableMapOf<Int, ContentRowUI>()

    // Procesamos los eventos y los agrupamos por subgénero
    this.events?.forEach { event ->
        event.idSubgenre?.toIntOrNull()?.let { subgenreId ->
            val row = rowsMap.getOrPut(subgenreId) {
                val subgenreName = subgenres.find { it.id == subgenreId }?.ds.orEmpty()
                ContentRowUI(
                    categoryId = subgenreId,
                    categoryName = subgenreName,
                    items = mutableListOf()
                )
            }
            (row.items as? MutableList)?.add(event.toContentEntityUI())
        }
    }

    // Hacemos lo mismo con los VODs, añadiéndolos a las filas existentes si el subgénero ya existe
    this.vods?.forEach { vod ->
        vod.subgenreById?.let { subgenreId ->
            val row = rowsMap.getOrPut(subgenreId) {
                val subgenreName = subgenres.find { it.id == subgenreId }?.ds.orEmpty()
                ContentRowUI(
                    categoryId = subgenreId,
                    categoryName = subgenreName,
                    items = mutableListOf()
                )
            }
            (row.items as? MutableList)?.add(vod.toContentEntityUI())
        }
    }

    // Obtenemos la lista de filas desde el mapa y la ordenamos si es necesario
    var contentRows = rowsMap.values.toList()

    // Cortamos las filas a no más de MAX_ELEMENTS_PER_ROW y añadimos el flag `loadMore`
    contentRows = contentRows.map { contentRow ->
        contentRow.copy(
            loadMore = contentRow.items.size > UIConstant.MAX_ELEMENTS_PER_ROW,
            items = contentRow.items.take(UIConstant.MAX_ELEMENTS_PER_ROW)
        )
    }

    return contentRows
}


fun GetBrandedContentResponse.toContentUIRows(
    subgenres: List<Subgenre>,
    isAdult: Boolean = false
): List<ContentRowUI> {
    val rowsMap = mutableMapOf<Int, ContentRowUI>()

    this.vods?.forEach { vod ->
        vod.idSubgenre?.toIntOrNull()?.let { subgenreId ->
            val row = rowsMap.getOrPut(subgenreId) {
                val subgenreName = subgenres.find { it.id == subgenreId }?.ds.orEmpty()
                ContentRowUI(
                    categoryId = subgenreId,
                    categoryName = subgenreName,
                    items = mutableListOf()
                )
            }
            (row.items as? MutableList)?.add(vod.toContentEntityUI())
        }
    }

    this.series?.forEach { serie ->
        serie.subgenreById?.let { subgenreId ->
            val row = rowsMap.getOrPut(subgenreId) {
                val subgenreName = subgenres.find { it.id == subgenreId }?.ds.orEmpty()
                ContentRowUI(
                    categoryId = subgenreId,
                    categoryName = subgenreName,
                    items = mutableListOf()
                )
            }
            (row.items as? MutableList)?.add(serie.toContentEntityUI())
        }
    }

    val featuredRow = this.featured?.mapNotNull { it.toContentEntityUI() }
        ?.takeIf { it.isNotEmpty() }
        ?.let { items ->
            ContentRowUI(
                categoryName = "Eventos Destacados",
                items = items,
                isFeatured = true
            )
        }

    var contentRows = rowsMap.values.toList()

    //Cortar las filas a no más de 15 elementos por fila y añadir el loadmore
    contentRows = contentRows.map { contentRow ->
        contentRow.copy(
            loadMore = contentRow.items.size > UIConstant.MAX_ELEMENTS_PER_ROW,
            items = contentRow.items.take(UIConstant.MAX_ELEMENTS_PER_ROW)
        )
    }

    return if (featuredRow != null) {
        listOf(featuredRow) + contentRows
    } else {
        contentRows
    }
}


fun List<ContentRowUI>.insertFeatured(
    featured: List<HomeFeatured>
): List<ContentRowUI> {
    ContentRowUI(
        categoryId = getRandomHashCode(),
        categoryName = "Eventos Destacados",
        items = featured.mapNotNull { it.toContentEntityUI() },
        isFeatured = true
    ).let {
        return listOf(it) + this
    }
}

fun List<ContentRowUI>.insertBookmarks(
    bookmarks: List<Bookmark>
): List<ContentRowUI> {
    if (bookmarks.isNotEmpty()) {
        ContentRowUI(
            categoryId = getRandomHashCode(),
            categoryName = "Seguir viendo",
            items = bookmarks.mapNotNull { it.toContentEntityUI() }
        ).let {
            return listOf(it) + this
        }
    } else {
        return this
    }
}

fun List<ContentRowUI>.insertRecommended(
    recommended: List<Recommended>
): List<ContentRowUI> {
    if (recommended.isNotEmpty()) {
        ContentRowUI(
            categoryId = getRandomHashCode(),
            categoryName = "Recomendado para ti",
            items = recommended.mapNotNull { it.toContentEntityUI() }
        ).let {
            return listOf(it) + this
        }
    } else {
        return this
    }
}

fun List<ContentRowUI>.insertMostWatched(
    mostWatched: List<MostWatchedContent>
): List<ContentRowUI> {
    if (mostWatched.isNotEmpty()) {
        ContentRowUI(
            categoryId = getRandomHashCode(),
            categoryName = "Más visto",
            items = mostWatched.mapNotNull { it.toContentEntityUI() }
        ).let {
            return listOf(it) + this
        }
    } else {
        return this
    }
}

fun List<ContentRowUI>.insertChannelRow(channels: List<Channel>?): List<ContentRowUI> {
    channels?.let {
        ContentRowUI(
            categoryId = getRandomHashCode(),
            categoryName = "Canales",
            items = it.map { channel -> channel.toContentEntityUI() }
        ).let { row ->
            return listOf(row) + this
        }
    }
    return this
}


//-------------region EXPANDED CATEGORY-----------------
fun GetBrandedContentResponse.toContentEntityUIList(): List<ContentEntityUI> {
    return this.vods.orEmpty().mapNotNull { it.toContentEntityUI() } + this.events.orEmpty()
        .map { it.toContentEntityUI() }
}

fun GetOtherContentResponse.toContentEntityUIList(): List<ContentEntityUI> {
    return this.vods.orEmpty().map { it.toContentEntityUI() } + this.events.orEmpty()
        .map { it.toContentEntityUI() }
}

fun GetBrandedContentResponse.findContent(identifier: ContentIdentifier): Any? {
    return when (identifier) {
        is ContentIdentifier.VoD -> this.vods?.find { it.getId() == identifier.id }
        is ContentIdentifier.Event -> this.events?.find { it.getId() == identifier.id }
        else -> null
    }
}

fun GetOtherContentResponse.findContent(identifier: ContentIdentifier): Any? {
    return when (identifier) {
        is ContentIdentifier.VoD -> this.vods?.find { it.getId() == identifier.id }
        is ContentIdentifier.Event -> this.events?.find { it.getId() == identifier.id }
        else -> null
    }
}
//-------------endregion EXPANDED CATEGORY-----------------

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

//---------------region EPG-------------
fun LocalDate.toDateSelectorResId(): Int? {
    val now = LocalDate.now()
    return when {
        this == now.minusDays(2) -> R.string.day_before_yesterday
        this == now.minusDays(1) -> R.string.yesterday
        this == now -> R.string.today
        this == now.plusDays(1) -> R.string.tomorrow
        this == now.plusDays(2) -> R.string.day_after_tomorrow
        else -> null
    }
}
//----------------endregion EPG--------------

