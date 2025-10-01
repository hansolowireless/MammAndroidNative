package com.mamm.mammapps.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.extension.toZonedDateTimeEPG
import com.mamm.mammapps.data.model.metadata.Metadata
import com.mamm.mammapps.data.model.player.WatermarkInfo
import com.mamm.mammapps.data.model.section.TbContentItem
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

data class GetHomeContentResponse(
    @SerializedName("timeGenerated") val timeGenerated: String? = null,
    @SerializedName("featured") val featured: List<FeaturedItem>? = null,
    @SerializedName("channels") val channels: List<Channel>? = null,
    @SerializedName("contents") val contents: List<VoD>? = null,
    @SerializedName("genres") val genres: List<Genre>? = null,
    @SerializedName("categories") val categories: List<Category>? = null,
    @SerializedName("events") val events: List<Event>? = null,
    @SerializedName("series") val series: List<Serie>? = null
)

data class FeaturedItem(
    @SerializedName("subgenreById") val subgenreById: Any? = null,
    @SerializedName("featured") val featured: Int? = null,
    @SerializedName("urlLoop") val urlLoop: String? = null,
    @SerializedName("logoTransitions") val logoTransitions: List<LogoTransition>? = null,
    @SerializedName("format") val format: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("fcIni") val fcIni: String? = null,
    @SerializedName("logoURL") val logoURL: String? = null,
    @SerializedName("duration") val duration: Any? = null,
    @SerializedName("idPpal") val idPpal: Any? = null,
    @SerializedName("urlLoopMpd") val urlLoopMpd: String? = null,
    @SerializedName("formatid") val formatid: String? = null,
    @SerializedName("fcEnd") val fcEnd: String? = null,
    @SerializedName("subtitle") val subtitle: String? = null,
    @SerializedName("channelById") val channelById: Any? = null,
    @SerializedName("deliveryURL") val deliveryURL: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("items") val items: String? = null,
    @SerializedName("parental") val parental: Any? = null
)

@Parcelize
data class LogoTransition(
    @SerializedName("url") val url: String? = null
) : Parcelable

@Parcelize
data class Channel(
    @SerializedName("freeaccess") val freeaccess: Int? = null,
    @SerializedName("channel_position") val channelPosition: Int? = null,
    @SerializedName("featured") val featured: Int? = null,
    @SerializedName("timeshiftOffset") val timeshiftOffset: Int? = null,
    @SerializedName("logoTransitions") val logoTransitions: List<LogoTransition>? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("drmUrl") val drmUrl: String? = null,
    @SerializedName("radio") val radio: Int? = null,
    @SerializedName("channelGenre") val channelGenre: String? = null,
    @SerializedName("premium") val premium: Int? = null,
    @SerializedName("timeshift") val timeshift: Boolean? = null,
    @SerializedName("fingerprint") val fingerprint: Boolean? = null,
    @SerializedName("fingerprintPosition") val fingerprintPosition: String? = null,
    @SerializedName("deliveryURL") var deliveryURL: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("catchup_hours") val catchupHours: Int? = null,
    @SerializedName("drmProvider") val drmProvider: String? = null,
    @SerializedName("epg") val epg: Int? = null,
    @SerializedName("parental") val parental: Int? = null,
    @SerializedName("channelGenreId") val channelGenreId: Int? = null,
    @SerializedName("cutvInitOffset") val cutvInitOffset: Int? = null,
    @SerializedName("isPornChannel") val isPornChannel: Boolean? = null,
    @SerializedName("logoURL") val logoURL: String? = null,
    @SerializedName("fingerprintFrequency") val fingerprintFrequency: Int? = null,
    @SerializedName("cutvEndOffset") val cutvEndOffset: Int? = null,
    @SerializedName("idPpal") val idPpal: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("logoTitleURL") val logoTitleURL: String? = null,
    @SerializedName("fingerprintDuration") val fingerprintDuration: Int? = null,
    @SerializedName("watermark") val watermark: WatermarkInfo? = null,
    var position : Int = 0,
    var fingerPrintText: String? = null
): Parcelable

@Parcelize
data class VoD(
    @SerializedName("content_logo") val contentLogo: String? = null,
    @SerializedName("content_logo_tilte") val contentLogoTilte: String? = null,
    @SerializedName("subgenreById") val subgenreById: Int? = null,
    @SerializedName("tbContentItems") val tbContentItems: List<TbContentItem>? = null,
    @SerializedName("logoTransitions") val logoTransitions: List<LogoTransition>? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("logoURL") val logoURL: String? = null,
    @SerializedName("duration") val duration: Int? = null,
    @SerializedName("expiryDate") val expiryDate: String? = null,
    @SerializedName("posterURL") val posterURL: String? = null,
    @SerializedName("provider") val provider: String? = null,
    @SerializedName("shortDesc") val shortDesc: String? = null,
    @SerializedName("deliveryURL") val deliveryURL: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("content_logo_500") val contentLogo500: String? = null,
    @SerializedName("longDesc") val longDesc: String? = null,
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("parental") val parental: Int? = null,
    var metadata: Metadata? = null
): Parcelable

data class Genre(
    @SerializedName("subgenres") val subgenres: List<Subgenre>? = null,
    @SerializedName("logo") val logo: Any? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("ds") val ds: String? = null
)

data class Subgenre(
    @SerializedName("descripcion") val descripcion: Any? = null,
    @SerializedName("logo") val logo: Any? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("ds") val ds: String? = null
)

data class Category(
    @SerializedName("catchup_row") val catchupRow: Boolean? = null,
    @SerializedName("pos") val pos: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("load_more") val loadMore: Boolean? = null,
    @SerializedName("order") val order: List<OrderItem>? = null
)

data class OrderItem(
    @SerializedName("pos") val pos: Int? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("type") val type: String? = null
)

@Parcelize
data class Event(
    @SerializedName("subgenreById") val subgenreById: Int? = null,
    @SerializedName("featured") val featured: Int? = null,
    @SerializedName("logoTransitions") val logoTransitions: List<LogoTransition>? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("fcIni") val fcIni: String? = null,
    @SerializedName("logoURL") val logoURL: String? = null,
    @SerializedName("duration") val duration: Int? = null,
    @SerializedName("idPpal") val idPpal: Int? = null,
    @SerializedName("fcEnd") val fcEnd: String? = null,
    @SerializedName("subtitle") val subtitle: String? = null,
    @SerializedName("deliveryURL") val deliveryURL: String? = null,
    @SerializedName("channelById") val channelById: Int? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("items") val items: String? = null,
    @SerializedName("parental") val parental: Int? = null
) : Parcelable {
    // Propiedades calculadas para fechas
    val startDateTime: ZonedDateTime?
        get() = fcIni?.toZonedDateTimeEPG()

    val endDateTime: ZonedDateTime?
        get() = fcEnd?.toZonedDateTimeEPG()
}

@Parcelize
data class Serie(
    @SerializedName("subgenreById") val subgenreById: Int? = null,
    @SerializedName("featured") val featured: Int? = null,
    @SerializedName("serie_logo_url") val serieLogoUrl: String? = null,
    @SerializedName("active") val active: Int? = null,
    @SerializedName("shortDesc") val shortDesc: String? = null,
    @SerializedName("logoTitleURL") val logoTitleURL: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("longDesc") val longDesc: String? = null
) : Parcelable