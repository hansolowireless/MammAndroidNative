package com.mamm.mammapps.data.model.serie

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.section.TbContentItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class Episode(
    @SerializedName("content_logo")
    val contentLogo: String? = null,

    @SerializedName("content_logo_tilte")
    val contentLogoTitle: String? = null,

    @SerializedName("tbContentLanguages")
    val tbContentLanguages: List<TbContentLanguage>? = null,

    @SerializedName("id_operator")
    val idOperator: String? = null,

    @SerializedName("tbContentItems")
    val tbContentItems: List<TbContentItem>? = null,

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

    @SerializedName("retry")
    val retry: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("start_date")
    val startDate: String? = null,

    @SerializedName("id_subgenre")
    val idSubgenre: String? = null
) : Parcelable {

    fun getId() : Int {
        return idContent?.toIntOrNull() ?: 0
    }

    fun getTitle(): String {
        return tbContentLanguages?.first()?.title.orEmpty()
    }

    fun getDescription(): String {
        return tbContentLanguages?.firstOrNull()?.longDescription ?: ""
    }

    fun getShortDescription(): String {
        return tbContentLanguages?.firstOrNull()?.shortDescription ?: ""
    }
}