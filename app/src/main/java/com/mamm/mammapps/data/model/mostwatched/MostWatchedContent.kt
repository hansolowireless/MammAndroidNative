package com.mamm.mammapps.data.model.mostwatched

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.metadata.MetadataDto
import com.mamm.mammapps.data.model.section.TbContentItemDto
import com.mamm.mammapps.util.toZonedDateTimeEPG
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class MostWatchedContentDto(
    @SerializedName("type")
    val type: String? = null,

    @SerializedName("views")
    val views: Int? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("shortDesc")
    val shortDesc: String? = null,

    @SerializedName("longDesc")
    val longDesc: String? = null,

    @SerializedName("duration")
    val duration: Int? = null,

    @SerializedName("startDate")
    val startDate: String? = null,

    @SerializedName("expiryDate")
    val expiryDate: String? = null,

    @SerializedName("logoURL")
    val logoURL: String? = null,

    @SerializedName("poster_logo")
    val posterLogo: String? = null,

    @SerializedName("deliveryURL")
    val deliveryURL: String? = null,

    @SerializedName("channelId")
    val channelId: String? = null,

    @SerializedName("subgenreById")
    val subgenreById: Int? = null,

    @SerializedName("fcIni")
    val fcIni: String? = null,

    @SerializedName("fcEnd")
    val fcEnd: String? = null,

    @SerializedName("tbEventItems")
    val tbEventItems: List<TbContentItemDto>? = null
) : Parcelable {

    val startDateTime: ZonedDateTime?
        get() = fcIni?.toZonedDateTimeEPG()

    val endDateTime: ZonedDateTime?
        get() = fcEnd?.toZonedDateTimeEPG()

    fun getMetadata(): MetadataDto {
        return MetadataDto.fromTbContentItems(tbEventItems ?: emptyList())
    }

}