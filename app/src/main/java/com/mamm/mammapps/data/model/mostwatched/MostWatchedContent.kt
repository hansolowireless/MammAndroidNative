package com.mamm.mammapps.data.model.mostwatched

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.model.metadata.Metadata
import com.mamm.mammapps.data.model.section.TbContentItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class MostWatchedContent(
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
    val tbEventItems: List<TbContentItem>? = null
): Parcelable {

        fun getMetadata(): Metadata {
            return Metadata.fromTbContentItems(tbEventItems ?: emptyList())
        }

}