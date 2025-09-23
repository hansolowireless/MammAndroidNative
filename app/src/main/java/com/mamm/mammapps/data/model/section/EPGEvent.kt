package com.mamm.mammapps.data.model.section

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.extension.getCurrentDate
import com.mamm.mammapps.data.extension.toZonedDateTimeEPG
import com.mamm.mammapps.data.model.Metadata
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class EPGEvent(
    @SerializedName("featured")
    val featured: String? = null,

    @SerializedName("id_ppal_event")
    val idPpalEvent: String? = null,

    @SerializedName("event_logo_url_500")
    val eventLogoUrl500: String? = null,

    @SerializedName("tbEventItems")
    val tbEventItems: List<TbEventItem>? = null,

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
    val deliveryUrl: String? = null,

    @SerializedName("id_event")
    val idEvent: String? = null,

    @SerializedName("url_loop")
    val urlLoop: String? = null,

    @SerializedName("tbEventLogoTransitions")
    val tbEventLogoTransitions: List<String>? = null,

    @SerializedName("id_subgenre")
    val idSubgenre: String? = null
) : Parcelable {

    init {
        println("=== EPGEvent Created ===")
        println("idEvent: '$idEvent'")
        println("idChannel: '$idChannel'")
        println("featured: '$featured'")
        println("========================")
    }

    fun getId(): Int = idEvent?.toIntOrNull() ?: idEvent?.hashCode() ?: 0

    // Propiedades calculadas para fechas
    val startDateTime: ZonedDateTime?
        get() = fcIni?.toZonedDateTimeEPG()

    val endDateTime: ZonedDateTime?
        get() = fcEnd?.toZonedDateTimeEPG()

    // Métodos para verificar estado del evento
    fun hasStarted(): Boolean {
        return startDateTime?.let { getCurrentDate().isAfter(it) || getCurrentDate().isEqual(it) } ?: false
    }

    fun hasFinished(): Boolean {
        return endDateTime?.let { ZonedDateTime.now().isAfter(it) } ?: false
    }

    fun isLive(): Boolean {
        val start = startDateTime ?: return false
        val end = endDateTime ?: return false
        val now = ZonedDateTime.now()

        return (now.isAfter(start) || now.isEqual(start)) && now.isBefore(end)
    }

    fun getMetadata(): Metadata {
        return Metadata.fromTbEventItems(tbEventItems ?: emptyList())
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