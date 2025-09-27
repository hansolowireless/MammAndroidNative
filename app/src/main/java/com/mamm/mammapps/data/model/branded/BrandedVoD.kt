package com.mamm.mammapps.data.model.branded

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BrandedVod(
    @SerializedName("content_logo")
    val contentLogo: String? = null,

    @SerializedName("content_logo_tilte")
    val contentLogoTitle: String? = null,

    @SerializedName("tbContentLanguages")
    val contentLanguages: List<ContentLanguage>? = null,

    @SerializedName("id_operator")
    val idOperator: String? = null,

    @SerializedName("tbContentItems")
    val contentItems: List<ContentItem>? = null,

    @SerializedName("logoTransitions")
    val logoTransitions: List<LogoTransition>? = null,

    @SerializedName("expiry_date")
    val expiryDate: String? = null,

    @SerializedName("format")
    val format: String? = null,

    @SerializedName("poster_logo")
    val posterLogo: String? = null,

    @SerializedName("creation_date")
    val creationDate: String? = null,

    @SerializedName("origen")
    val origen: String? = null,

    @SerializedName("logoURL")
    val logoUrl: String? = null,

    @SerializedName("in_ftp")
    val inFtp: String? = null,

    @SerializedName("duration")
    val duration: String? = null,

    @SerializedName("path")
    val path: String? = null,

    @SerializedName("id_provider")
    val idProvider: String? = null,

    @SerializedName("id_content")
    val idContent: String? = null,

    @SerializedName("id_parental")
    val idParental: String? = null,

    @SerializedName("originalId")
    val originalId: String? = null,

    @SerializedName("content_logo_500")
    val contentLogo500: String? = null,

    @SerializedName("retry")
    val retry: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("start_date")
    val startDate: String? = null,

    @SerializedName("id_subgenre")
    val idSubgenre: String? = null
) : Parcelable {
    fun getId(): Int {
        return idContent?.toIntOrNull() ?: 0
    }

    fun getTitle(): String {
        return contentLanguages?.firstOrNull()?.title.orEmpty()
    }

    fun getDescription(): String {
        return contentLanguages?.firstOrNull()?.longDescription.orEmpty()
    }
}

@Parcelize
data class ContentLanguage(
    @SerializedName("short_description")
    val shortDescription: String? = null,

    @SerializedName("id_content")
    val idContent: String? = null,

    @SerializedName("id_language")
    val idLanguage: String? = null,

    @SerializedName("id_content_language")
    val idContentLanguage: String? = null,

    @SerializedName("long_description")
    val longDescription: String? = null,

    @SerializedName("title")
    val title: String? = null
) : Parcelable

@Parcelize
data class ContentItem(
    @SerializedName("id_content")
    val idContent: String? = null,

    @SerializedName("item_ds")
    val itemDs: String? = null,

    @SerializedName("id_content_item")
    val idContentItem: String? = null,

    @SerializedName("item_value")
    val itemValue: String? = null
) : Parcelable

@Parcelize
data class LogoTransition(
    @SerializedName("url")
    val url: String? = null
) : Parcelable