package com.mamm.mammapps.ui.mapper

import com.mamm.mammapps.R
import com.mamm.mammapps.domain.model.entity.Channel
import com.mamm.mammapps.domain.model.entity.Event
import com.mamm.mammapps.domain.model.Genre
import com.mamm.mammapps.domain.model.BrandedContent
import com.mamm.mammapps.domain.model.HomeContent
import com.mamm.mammapps.domain.model.OtherContent
import com.mamm.mammapps.domain.model.entity.Featured
import com.mamm.mammapps.domain.model.entity.Serie
import com.mamm.mammapps.domain.model.Subgenre
import com.mamm.mammapps.domain.model.entity.VoD
import com.mamm.mammapps.domain.model.bookmark.Bookmark
import com.mamm.mammapps.domain.model.memories.Memories
import com.mamm.mammapps.domain.model.memories.MemoryItem
import com.mamm.mammapps.domain.model.serie.SerieInfo
import com.mamm.mammapps.domain.model.serie.TbContentSeason
import com.mamm.mammapps.domain.model.topchannels.TopChannels
import com.mamm.mammapps.ui.constant.UIConstant
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
import kotlin.collections.isNotEmpty
import kotlin.collections.map

fun Any.toContentEntityUI(): ContentEntityUI? {
    return when (this) {
        is Channel -> this.toContentEntityUI()
        is VoD -> this.toContentEntityUI()
        is Event -> this.toContentEntityUI()
        is Serie -> this.toContentEntityUI()
        is Bookmark -> this.toContentEntityUI()
        is Featured -> this.toContentEntityUI()
        else -> null
    }
}

fun Any.toContentToPlayUI(): ContentToPlayUI? {
    return when (this) {
        is Channel -> this.toContentToPlayUI()
        is VoD -> this.toContentToPlayUI()
        is Event -> this.toContentToPlayUI()
        is Featured -> this.toContentToPlayUI()
        is TbContentSeason -> this.contentDetails?.toContentToPlayUI()
        is Bookmark -> this.toContentToPlayUI()
        is MemoryItem -> this.toContentToPlayUI()
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
    identifier = ContentIdentifier.VoD(getId()),
    imageUrl = (posterURL?.takeIf { it.isNotBlank() } ?: logoURL).orEmpty(),
    horizontalImageUrl = (logoURL?.takeIf { it.isNotBlank() } ?: contentLogo).orEmpty(),
    title = title,
    aspectRatio = Ratios.VERTICAL,
    height = Dimensions.contentEntityHeight,
    detailInfo = DetailInfoUI(
        metadata = metadata,
        description = getDescription(),
        subgenreId = subgenreById
    )
)

fun Event.toContentEntityUI() = ContentEntityUI(
    identifier = ContentIdentifier.Event(getId()),
    imageUrl = (posterLogo?.takeIf { it.isNotBlank() } ?: eventLogoUrl500?.takeIf { it.isNotBlank() } ?: logoURL).orEmpty(),
    horizontalImageUrl = (eventLogoUrl?.takeIf { it.isNotBlank() } ?: logoURL).orEmpty(),
    title = title,
    aspectRatio = Ratios.VERTICAL,
    height = Dimensions.contentEntityHeight,
    detailInfo = DetailInfoUI(
        subtitle = subtitle,
        description = description,
        subgenreId = subgenreById,
        metadata = metadata,
        channelId = getChannelId()
    ),
    liveEventInfo = this.toLiveEventInfoUI()
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

fun Featured.toContentEntityUI(): ContentEntityUI? {
    val format = format ?: return null
    val id = id ?: return null
    val imageUrl = logoTransitions?.first()?.url ?: logoURL.orEmpty()

    return ContentEntityUI(
        identifier = ContentIdentifier.fromFeaturedFormat(
            format = format,
            id = id,
            channelById = channelById
        ),
        imageUrl = logoURL.orEmpty(),
        horizontalImageUrl = imageUrl,
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
            metadata = metadata
        ),
        aspectRatio = Ratios.VERTICAL,
        height = Dimensions.contentEntityHeight,
        customContentType = CustomizedContent.BookmarkType
    )
}



fun MemoryItem.toContentEntityUI(): ContentEntityUI {
    return ContentEntityUI(
        identifier = ContentIdentifier.VoD(this.id),
        title = this.title,
        imageUrl = this.thumbnail,
        horizontalImageUrl = this.thumbnail
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
fun VoD.toContentListUI() = ContentListUI(
    identifier = ContentIdentifier.VoD(getId()),
    imageUrl = (contentLogo?.takeIf { it.isNotBlank() } ?: posterURL?.takeIf { it.isNotBlank() } ?: logoURL).orEmpty(),
    title = title,
    detailInfo = DetailInfoUI(
        description = getDescription()
    )
)

fun Event.toContentListUI() = ContentListUI(
    identifier = ContentIdentifier.Event(getId()),
    imageUrl = (eventLogoUrl500?.takeIf { it.isNotBlank() } ?: logoURL).orEmpty(),
    title = title,
    detailInfo = DetailInfoUI(
        description = description
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
    identifier = ContentIdentifier.VoD(getId()),
    deliveryURL = this.deliveryURL.orEmpty(),
    title = title,
    imageUrl = (posterURL?.takeIf { it.isNotBlank() } ?: logoURL).orEmpty()
)

fun Event.toContentToPlayUI() = ContentToPlayUI(
    identifier = ContentIdentifier.Event(getId()),
    deliveryURL = this.deliveryURL.orEmpty(),
    title = title,
    imageUrl = (eventLogoUrl500?.takeIf { it.isNotBlank() } ?: eventLogoUrl?.takeIf { it.isNotBlank() } ?: logoURL).orEmpty(),
    //It's used to get the start and end dates in order to build the catchup URL
    epgEventInfo = this.toLiveEventInfoUI()
)

fun Featured.toContentToPlayUI(): ContentToPlayUI? {
    val format = format ?: return null
    val id = id ?: return null

    return ContentToPlayUI(
        identifier = ContentIdentifier.fromFeaturedFormat(format = format, id = id),
        deliveryURL = this.deliveryURL.orEmpty(),
        title = this.title.orEmpty(),
        imageUrl = this.logoURL.orEmpty(),
    )
}

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



fun MemoryItem.toContentToPlayUI() : ContentToPlayUI {
    return ContentToPlayUI(
        identifier = ContentIdentifier.VoD(this.id),
        title = this.title,
        deliveryURL = this.url,
        imageUrl = this.thumbnail,
        shouldCallCLM = false
    )
}


//------------------------LIVE EVENT INFO------------------------
fun Event.toLiveEventInfoUI(): LiveEventInfoUI = LiveEventInfoUI(
    title = this.title,
    logoURL = (this.eventLogoUrl500 ?: this.logoURL).orEmpty(),
    deliveryURL = this.deliveryURL.orEmpty(),
    eventStart = this.startDateTime,
    eventEnd = this.endDateTime,
    fatherChannelId = this.channelById
)
//----------endregion PLAYBACK----------------------

fun HomeContent.toContentUIRows(): List<ContentRowUI> {
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

fun OtherContent.toContentUIRows(
    subgenres: List<Subgenre>
): List<ContentRowUI> {
    val rowsMap = mutableMapOf<Int, ContentRowUI>()

    // Procesamos los eventos y los agrupamos por subgénero
    this.events?.forEach { event ->
        event.subgenreById?.let { subgenreId ->
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


fun BrandedContent.toContentUIRows(
    subgenres: List<Subgenre>
): List<ContentRowUI> {
    val rowsMap = mutableMapOf<Int, ContentRowUI>()

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

fun Memories.toContentUIRows(): List<ContentRowUI> {
    return this.sections?.mapIndexedNotNull { index, section ->
        if (section.items.isNotEmpty()) {
            ContentRowUI(
                categoryId = index,
                categoryName = section.title,
                items = section.items.map { it.toContentEntityUI() }
            )
        } else {
            null
        }
    } ?: emptyList()
}

fun List<ContentRowUI>.insertFeatured(
    featured: List<Featured>?
): List<ContentRowUI> {
    if (featured.isNullOrEmpty()) return this

    val featuredItems = featured.mapNotNull { it.toContentEntityUI() }
    val targetCategoryId = 2002

    // Comprobamos si la categoría 2002 ya existe en la lista
    val hasTargetCategory = this.any { it.categoryId == targetCategoryId }

    return if (hasTargetCategory) {
        // Si existe, mapeamos la lista para encontrarla y añadir los items al principio de esa fila
        this.map { row ->
            if (row.categoryId == targetCategoryId) {
                row.copy(
                    items = featuredItems + row.items,
                    isFeatured = true
                )
            } else {
                row
            }
        }
    } else {
        // Si NO existe la categoría 2002, crear una fila nueva e insertarla al principio de todo
        val newFeaturedRow = ContentRowUI(
            categoryId = targetCategoryId,
            categoryName = "Eventos Destacados",
            items = featuredItems,
            isFeatured = true
        )
        listOf(newFeaturedRow) + this
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
    recommended: List<Any>
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
    mostWatched: List<Any>
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

fun List<ContentRowUI>.insertTopChannels(
    topChannels: TopChannels?,
    availableChannels: List<Channel>?
): List<ContentRowUI> {
    if (topChannels == null || topChannels.channels.isEmpty() || availableChannels.isNullOrEmpty()) return this

    val topChannelIds = topChannels.channels.mapNotNull { it.channelId.toIntOrNull() }

    val sortedChannels = topChannelIds.mapNotNull { id ->
        availableChannels.find { it.id == id }
    }

    if (sortedChannels.isNotEmpty()) {
        ContentRowUI(
            categoryId = getRandomHashCode(),
            categoryName = "Mis canales favoritos",
            items = sortedChannels.map { it.toContentEntityUI() }
        ).let {
            return listOf(it) + this
        }
    }
    return this
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
fun BrandedContent.toContentEntityUIList(): List<ContentEntityUI> {
    return this.vods.orEmpty().mapNotNull { it.toContentEntityUI() } + this.events.orEmpty()
        .map { it.toContentEntityUI() }
}

fun OtherContent.toContentEntityUIList(): List<ContentEntityUI> {
    return this.vods.orEmpty().map { it.toContentEntityUI() } + this.events.orEmpty()
        .map { it.toContentEntityUI() }
}

fun BrandedContent.findContent(identifier: ContentIdentifier): Any? {
    return when (identifier) {
        is ContentIdentifier.VoD -> this.vods?.find { it.getId() == identifier.id }
        is ContentIdentifier.Event -> this.events?.find { it.getId() == identifier.id }
        else -> null
    }
}

fun OtherContent.findContent(identifier: ContentIdentifier): Any? {
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

fun SerieInfo.toSeasonUIList(): List<SeasonUI> {
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
fun List<Any>.toSimilarContentRow(): ContentRowUI {
    ContentRowUI(
        categoryName = "Contenido Similar",
        items = this.mapNotNull { it.toContentEntityUI() }
    ).let {
        return it
    }
}

fun Any.getId(): Int {
    return when (this) {
        is Channel -> this.id ?: 0
        is VoD -> this.getId()
        is Event -> this.getId()
        is Bookmark -> this.id ?: 0
        else -> 0
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

