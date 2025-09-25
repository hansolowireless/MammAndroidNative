package com.mamm.mammapps.ui.mapper

import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.data.model.Event
import com.mamm.mammapps.data.model.Genre
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.data.model.GetSeasonInfoResponse
import com.mamm.mammapps.data.model.section.SectionVod
import com.mamm.mammapps.data.model.Serie
import com.mamm.mammapps.data.model.VoD
import com.mamm.mammapps.domain.usecases.GetSeasonsInfoUseCase
import com.mamm.mammapps.ui.extension.adult
import com.mamm.mammapps.ui.extension.landscape
import com.mamm.mammapps.ui.extension.squared
import com.mamm.mammapps.ui.model.ContentEPGUI
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.ContentListUI
import com.mamm.mammapps.ui.model.ContentRowUI
import com.mamm.mammapps.ui.model.DetailInfoUI
import com.mamm.mammapps.ui.model.SeasonUI
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.model.player.FingerPrintInfoUI
import com.mamm.mammapps.ui.model.player.LiveEventInfoUI
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.Ratios
import com.mamm.mammapps.util.orRandom

//--------------------region Home------------------------
fun Channel.toContentEntityUI() = ContentEntityUI(
    imageUrl = logoURL?.landscape() ?: "",
    title = name ?: "",
    detailInfo = DetailInfoUI(squareLogo = logoURL?.squared()),
    identifier = ContentIdentifier.Channel(id.orRandom())
)

fun VoD.toContentEntityUI() = ContentEntityUI(
    imageUrl = posterURL ?: "",
    title = title ?: "",
    aspectRatio = Ratios.VERTICAL,
    height = Dimensions.contentEntityHeight,
    identifier = ContentIdentifier.VoD(id.orRandom()),
    detailInfo = DetailInfoUI(
        metadata = metadata,
        description = longDesc.orEmpty()
    )
)

fun Event.toContentEntityUI() = ContentEntityUI(
    imageUrl = logoURL.orEmpty(),
    title = title.orEmpty(),
    aspectRatio = Ratios.VERTICAL,
    height = Dimensions.contentEntityHeight,
    identifier = ContentIdentifier.Event(id.orRandom()),
    detailInfo = DetailInfoUI(
        subtitle = subtitle.orEmpty(),
        description = description.orEmpty()
    )
)

fun Serie.toContentEntityUI() = ContentEntityUI(
    imageUrl = serieLogoUrl.orEmpty(),
    title = title.orEmpty(),
    detailInfo = DetailInfoUI(
        description = longDesc.orEmpty()
    ),
    aspectRatio = Ratios.HORIZONTAL,
    height = Dimensions.channelEntityHeight,
    identifier = ContentIdentifier.Serie(id.orRandom())
)

fun EPGEvent.toContentEntityUI(isAdult: Boolean = false) = ContentEntityUI(
    imageUrl = (posterLogo?.takeIf { it.isNotBlank() }
        ?: eventLogoUrl500?.takeIf { it.isNotBlank() })
        .orEmpty().adult(isAdult),
    title = getTitle(),
    aspectRatio = Ratios.VERTICAL,
    height = Dimensions.contentEntityHeight,
    identifier = ContentIdentifier.Event(
        getId()
    ),
    detailInfo = DetailInfoUI(
        metadata = getMetadata(),
        description = getDescription()
    )
)

fun SectionVod.toContentEntityUI(isAdult: Boolean = false) = ContentEntityUI(
    imageUrl = (posterLogo?.takeIf { it.isNotBlank() }
        ?: eventLogoUrl500?.takeIf { it.isNotBlank() })
        .orEmpty().adult(isAdult),
    title = getTitle(),
    aspectRatio = Ratios.VERTICAL,
    height = Dimensions.contentEntityHeight,
    identifier = ContentIdentifier.VoD(
        getId()
    ),
    detailInfo = DetailInfoUI(
        description = getDescription(),
        metadata = getMetadata()
    )
)
//--------------------endregion Home------------------------


//-------------------------EPG----------------------------
fun Channel.toContentEPGUI() = ContentEPGUI(
    identifier = ContentIdentifier.Channel(id.orRandom()),
    title = name ?: "",
    imageUrl = logoURL?.squared() ?: ""
)

//-------------------------region ContentAsList----------------------------
fun EPGEvent.toContentListUI () = ContentListUI(
    identifier = ContentIdentifier.Event(getId().orRandom()),
    imageUrl = eventLogoUrl500?.takeIf { it.isNotBlank() }
        .orEmpty(),
    title = getTitle(),
)

//-------------------------endregion ContentAsList-------------------------


//-----------region PLAYBACK-------------------
fun Channel.toContentToPlayUI() = ContentToPlayUI(
    identifier = ContentIdentifier.Channel(id.orRandom()),
    deliveryURL = this.deliveryURL ?: "",
    title = name ?: "",
    imageUrl = logoURL?.squared() ?: "",
    isTimeshift = this.timeshift ?: false,
    fingerprintInfo = FingerPrintInfoUI(
        enabled = this.fingerprint ?: false,
        interval = this.fingerprintFrequency,
        duration = this.fingerprintDuration,
        position = this.fingerprintPosition ?: "random",
        text = "Watermark"
    ),
    watermarkInfo = this.watermark
)

fun VoD.toContentToPlayUI() = ContentToPlayUI(
    identifier = ContentIdentifier.VoD(id.orRandom()),
    deliveryURL = this.deliveryURL ?: "",
    title = title ?: "",
    imageUrl = posterURL ?: ""
)

fun Event.toContentToPlayUI() = ContentToPlayUI(
    identifier = ContentIdentifier.Event(id.orRandom()),
    deliveryURL = this.deliveryURL.orEmpty(),
    title = title ?: "",
    imageUrl = logoURL ?: "",
    //It's used to get the start and end dates in order to build the catchup URL
    epgEventInfo = this.toLiveEventInfoUI()
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

fun GetOtherContentResponse.toContentUIRows(
    genre: Genre,
    isAdult: Boolean = false
): List<ContentRowUI> {
    val rows = mutableListOf<ContentRowUI>()

    genre.subgenres?.forEach { sub ->
        // Filtramos los events de este subgénero (si aplica)
        val subEvents = events.orEmpty().filter { it.idSubgenre == sub.id.toString() }
        val subVods = vods.orEmpty().filter { it.idSubgenre == sub.id.toString() }

        // Convertimos a ContentEntityUI
        val items = subEvents.map { it.toContentEntityUI(isAdult = isAdult) } +
                subVods.map { it.toContentEntityUI(isAdult = isAdult) }

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
    val list = this.tbSeasons?.map{ tbSeason ->

        val episodes : List<ContentEntityUI> = tbSeason.tbContentSeasons?.map { tbContentSeason ->
            ContentEntityUI(
                identifier = ContentIdentifier.VoD(tbContentSeason.contentDetails?.getId().orRandom()),
                imageUrl = tbContentSeason.contentDetails?.contentLogo.orEmpty(),
                title = tbContentSeason.contentDetails?.getTitle().orEmpty(),
            )
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

