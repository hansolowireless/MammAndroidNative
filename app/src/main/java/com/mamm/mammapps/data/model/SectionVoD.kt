package com.mamm.mammapps.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

// Assuming Vod has similar structure to Event, but you may need to adjust based on actual structure
@Parcelize
data class SectionVod(
    @SerializedName("featured")
    val featured: String? = null,

    @SerializedName("id_ppal_event")
    val idPpalEvent: String? = null,

    @SerializedName("event_logo_url_500")
    val eventLogoUrl500: String? = null,

    @SerializedName("tbEventItems")
    val tbEventItems: List<TbContentItem>? = null,

    @SerializedName("event_logo_title_url")
    val eventLogoTitleUrl: String? = null,

    @SerializedName("id_channel")
    val idChannel: String? = null,

    @SerializedName("episode")
    val episode: String? = null,

    @SerializedName("url_loop_mpd")
    val urlLoopMpd: String? = null,

    @SerializedName("poster_logo")
    val posterLogo: String? = null,

    @SerializedName("event_logo_url")
    val eventLogoUrl: String? = null,

    @SerializedName("fc_ini")
    val fcIni: String? = null,

    @SerializedName("duration")
    val duration: String? = null,

    @SerializedName("in_active")
    val inActive: String? = null,

    @SerializedName("fc_end")
    val fcEnd: String? = null,

    @SerializedName("id_parental")
    val idParental: String? = null,

    @SerializedName("tbEventLanguages")
    val tbEventLanguages: List<TbEventLanguage>? = null,

    @SerializedName("deliveryURL")
    val deliveryURL: String? = null,

    @SerializedName("id_event")
    val idEvent: String? = null,

    @SerializedName("url_loop")
    val urlLoop: String? = null,

    @SerializedName("tbEventLogoTransitions")
    val tbEventLogoTransitions: List<String>? = null,

    @SerializedName("id_subgenre")
    val idSubgenre: String? = null
) : Parcelable {

    @IgnoredOnParcel
    val id : Int = idEvent?.toIntOrNull() ?: idEvent.hashCode()

    fun getMetadata(): Metadata {
        return Metadata.fromTbContentItems(tbEventItems ?: emptyList())
    }

    fun getTitle(): String {
        return tbEventLanguages?.firstOrNull()?.title ?: ""
    }

    fun getDescription(): String {
        return tbEventLanguages?.firstOrNull()?.description ?: ""
    }

    fun getSubtitle(): String {
        return tbEventLanguages?.firstOrNull()?.subtitle ?: ""
    }
}