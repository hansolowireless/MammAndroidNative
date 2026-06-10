package com.mamm.mammapps.data.model.bookmark

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.metadata.MetadataDto
import com.mamm.mammapps.data.model.section.TbContentItemDto
import com.mamm.mammapps.util.toZonedDateTimeEPG
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

sealed class BookmarkContentDto : Parcelable {
    abstract val type: String?
    abstract val id: Int?
    abstract val title: String?
    abstract val shortDesc: String?
    abstract val longDesc: String?
    abstract val duration: String?
    abstract val startDate: String?
    abstract val expiryDate: String?
    abstract val logoURL: String?
    abstract val posterLogo: String?
    abstract val deliveryURL: String?
    abstract val channelId: String?
    abstract val currentTime: Int?
    abstract val fcStored: String?
    abstract val subgenreById: Int?
    abstract val fcIni: String?
    abstract val fcEnd: String?
    abstract val tbContentItems: List<TbContentItemDto>?
    abstract val tbEventItems: List<TbContentItemDto>?

    // Propiedades calculadas
    val startDateTime: ZonedDateTime?
        get() = fcIni?.toZonedDateTimeEPG()

    val endDateTime: ZonedDateTime?
        get() = fcEnd?.toZonedDateTimeEPG()

    fun getMetadata(): MetadataDto {
        return MetadataDto.fromTbContentItems(tbEventItems ?: tbContentItems ?: emptyList())
    }
}

@Parcelize
data class BookmarkDto(
    @SerializedName("type") override val type: String? = null,
    @SerializedName("id") override val id: Int? = null,
    @SerializedName("title") override val title: String? = null,
    @SerializedName("shortDesc") override val shortDesc: String? = null,
    @SerializedName("longDesc") override val longDesc: String? = null,
    @SerializedName("duration") override val duration: String? = null,
    @SerializedName("startDate") override val startDate: String? = null,
    @SerializedName("expiryDate") override val expiryDate: String? = null,
    @SerializedName("logoURL") override val logoURL: String? = null,
    @SerializedName("poster_logo") override val posterLogo: String? = null,
    @SerializedName("deliveryURL") override val deliveryURL: String? = null,
    @SerializedName("channelId") override val channelId: String? = null,
    @SerializedName("currentTime") override val currentTime: Int? = null,
    @SerializedName("fcStored") override val fcStored: String? = null,
    @SerializedName("subgenreById") override val subgenreById: Int? = null,
    @SerializedName("fcIni") override val fcIni: String? = null,
    @SerializedName("fcEnd") override val fcEnd: String? = null,
    @SerializedName("tbEventItems") override val tbEventItems: List<TbContentItemDto>? = null,
    @SerializedName("tbContentItems") override val tbContentItems: List<TbContentItemDto>? = null
) : BookmarkContentDto()

@Parcelize
data class RecommendedDto(
    @SerializedName("type") override val type: String? = null,
    @SerializedName("id") override val id: Int? = null,
    @SerializedName("title") override val title: String? = null,
    @SerializedName("shortDesc") override val shortDesc: String? = null,
    @SerializedName("longDesc") override val longDesc: String? = null,
    @SerializedName("duration") override val duration: String? = null,
    @SerializedName("startDate") override val startDate: String? = null,
    @SerializedName("expiryDate") override val expiryDate: String? = null,
    @SerializedName("logoURL") override val logoURL: String? = null,
    @SerializedName("poster_logo") override val posterLogo: String? = null,
    @SerializedName("deliveryURL") override val deliveryURL: String? = null,
    @SerializedName("channelId") override val channelId: String? = null,
    @SerializedName("currentTime") override val currentTime: Int? = null,
    @SerializedName("fcStored") override val fcStored: String? = null,
    @SerializedName("subgenreById") override val subgenreById: Int? = null,
    @SerializedName("fcIni") override val fcIni: String? = null,
    @SerializedName("fcEnd") override val fcEnd: String? = null,
    @SerializedName("tbEventItems") override val tbEventItems: List<TbContentItemDto>? = null,
    @SerializedName("tbContentItems") override val tbContentItems: List<TbContentItemDto>? = null
) : BookmarkContentDto()
