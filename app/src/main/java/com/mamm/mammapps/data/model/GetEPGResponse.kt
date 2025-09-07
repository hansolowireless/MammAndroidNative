package com.mamm.mammapps.data.model

import com.google.gson.annotations.SerializedName

data class GetEPGResponse(
    @SerializedName("events")
    val events: List<EPGEvent>? = null
)

data class EPGEvent(
    @SerializedName("featured")
    val featured: String? = null,

    @SerializedName("id_ppal_event")
    val idPpalEvent: String? = null,

    @SerializedName("event_logo_url_500")
    val eventLogoUrl500: String? = null,

    @SerializedName("tbEventItems")
    val tbEventItems: List<EventItem>? = null,

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
    val tbEventLanguages: List<EventLanguage>? = null,

    @SerializedName("deliveryURL")
    val deliveryUrl: String? = null,

    @SerializedName("id_event")
    val idEvent: String? = null,

    @SerializedName("url_loop")
    val urlLoop: String? = null,

    @SerializedName("tbEventLogoTransitions")
    val tbEventLogoTransitions: List<Any>? = null,

    @SerializedName("id_subgenre")
    val idSubgenre: String? = null
)

data class EventItem(
    @SerializedName("item_ds")
    val itemDs: String? = null,

    @SerializedName("id_event_item")
    val idEventItem: String? = null,

    @SerializedName("id_event")
    val idEvent: String? = null,

    @SerializedName("item_value")
    val itemValue: String? = null
)

data class EventLanguage(
    @SerializedName("subtitle")
    val subtitle: String? = null,

    @SerializedName("id_language")
    val idLanguage: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("id_event")
    val idEvent: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("id_event_lang")
    val idEventLang: String? = null
)