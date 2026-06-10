package com.mamm.mammapps.data.model.section

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.metadata.MetadataDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class SectionVodDto(
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

    @SerializedName("deliveryURL")
    val deliveryURL: String? = null,

    @SerializedName("provider")
    val provider: String? = null,

    @SerializedName("parental")
    val parental: Int? = null,

    @SerializedName("startDate")
    val startDate: String? = null,

    @SerializedName("expiryDate")
    val expiryDate: String? = null,

    @SerializedName("logoURL")
    val logoURL: String? = null,

    @SerializedName("posterURL")
    val posterURL: String? = null,

    @SerializedName("content_logo")
    val contentLogo: String? = null,

    @SerializedName("content_logo_500")
    val contentLogo500: String? = null,

    @SerializedName("content_logo_tilte")
    val contentLogoTitle: String? = null,

    @SerializedName("subgenreById")
    val subgenreById: Int? = null,

    @SerializedName("tbContentItems")
    val tbContentItems: List<TbContentItemDto>? = null,

    @SerializedName("logoTransitions")
    val logoTransitions: List<LogoTransitionDto>? = null
) : Parcelable {

    fun getId(): Int = id ?: 0

    fun getMetadata(): MetadataDto {
        return MetadataDto.fromTbContentItems(tbContentItems ?: emptyList())
    }

    fun getDescription(): String {
        return longDesc.orEmpty()
    }

}

@Parcelize
data class LogoTransitionDto(
    @SerializedName("url")
    val url: String? = null
) : Parcelable