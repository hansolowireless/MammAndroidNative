package com.mamm.mammapps.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.metadata.MetadataDto
import com.mamm.mammapps.data.model.player.WatermarkInfoDto
import com.mamm.mammapps.util.toZonedDateTimeEPG
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

data class GetHomeContentResponseDto(
    @SerializedName("timeGenerated") val timeGenerated: String? = null,
    @SerializedName("featured") val featured: List<HomeFeaturedDto>? = null,
    @SerializedName("channels") val channels: List<ChannelDto>? = null,
    @SerializedName("contents") val contents: List<VoDDto>? = null,
    @SerializedName("genres") val genres: List<GenreDto>? = null,
    @SerializedName("categories") val categories: List<CategoryDto>? = null,
    @SerializedName("events") val events: List<EventDto>? = null,
    @SerializedName("series") val series: List<SerieDto>? = null
)

@Parcelize
data class HomeFeaturedDto(
    @SerializedName("subgenreById") val subgenreById: Int? = null,
    @SerializedName("featured") val featured: Int? = null,
    @SerializedName("urlLoop") val urlLoop: String? = null,
    @SerializedName("logoTransitions") val logoTransitions: List<LogoTransitionDto>? = null,
    @SerializedName("format") val format: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("fcIni") val fcIni: String? = null,
    @SerializedName("logoURL") val logoURL: String? = null,
    @SerializedName("duration") val duration: Int? = null,
    @SerializedName("idPpal") val idPpal: Int? = null,
    @SerializedName("urlLoopMpd") val urlLoopMpd: String? = null,
    @SerializedName("formatid") val formatid: String? = null,
    @SerializedName("fcEnd") val fcEnd: String? = null,
    @SerializedName("subtitle") val subtitle: String? = null,
    @SerializedName("channelById") val channelById: Int? = null,
    @SerializedName("deliveryURL") val deliveryURL: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("items") val items: String? = null,
    @SerializedName("parental") val parental: Int? = null
) : Parcelable

@Parcelize
data class LogoTransitionDto(
    @SerializedName("url") val url: String? = null
) : Parcelable

@Parcelize
data class ChannelDto(
    @SerializedName("freeaccess") val freeaccess: Int? = null,
    @SerializedName("channel_position") val channelPosition: Int? = null,
    @SerializedName("featured") val featured: Int? = null,
    @SerializedName("timeshiftOffset") val timeshiftOffset: Int? = null,
    @SerializedName("logoTransitions") val logoTransitions: List<LogoTransitionDto>? = null,
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
    @SerializedName("isPornChannel") val isPornChannel: Boolean? = false,
    @SerializedName("logoURL") val logoURL: String? = null,
    @SerializedName("fingerprintFrequency") val fingerprintFrequency: Int? = null,
    @SerializedName("cutvEndOffset") val cutvEndOffset: Int? = null,
    @SerializedName("idPpal") val idPpal: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("logoTitleURL") val logoTitleURL: String? = null,
    @SerializedName("fingerprintDuration") val fingerprintDuration: Int? = null,
    @SerializedName("watermark") val watermark: WatermarkInfoDto? = null,
    var position : Int = Int.MAX_VALUE,
    var fingerPrintText: String? = null
): Parcelable

@Parcelize
data class VoDDto(
    @SerializedName("content_logo") val contentLogo: String? = null,
    @SerializedName("content_logo_tilte") val contentLogoTilte: String? = null,
    @SerializedName("subgenreById") val subgenreById: Int? = null,
    @SerializedName("tbContentItems") val tbContentItems: List<com.mamm.mammapps.data.model.section.TbContentItemDto>? = null,
    @SerializedName("logoTransitions") val logoTransitions: List<LogoTransitionDto>? = null,
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
    var metadata: MetadataDto? = null
): Parcelable

data class GenreDto(
    @SerializedName("subgenres") val subgenres: List<SubgenreDto>? = null,
    @SerializedName("logo") val logo: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("ds") val ds: String? = null
)

data class SubgenreDto(
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("logo") val logo: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("ds") val ds: String? = null
)

data class CategoryDto(
    @SerializedName("catchup_row") val catchupRow: Boolean? = null,
    @SerializedName("pos") val pos: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("load_more") val loadMore: Boolean = false,
    @SerializedName("order") val order: List<OrderItemDto>? = null
)

data class OrderItemDto(
    @SerializedName("pos") val pos: Int? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("type") val type: String? = null
)

@Parcelize
data class EventDto(
    @SerializedName("subgenreById") val subgenreById: Int? = null,
    @SerializedName("featured") val featured: Int? = null,
    @SerializedName("logoTransitions") val logoTransitions: List<LogoTransitionDto>? = null,
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
    val startDateTime: ZonedDateTime?
        get() = fcIni?.toZonedDateTimeEPG()

    val endDateTime: ZonedDateTime?
        get() = fcEnd?.toZonedDateTimeEPG()
}

@Parcelize
data class SerieDto(
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