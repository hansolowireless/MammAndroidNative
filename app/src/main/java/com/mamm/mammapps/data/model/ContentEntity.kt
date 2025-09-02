//package com.mamm.mammapps.data.model
//
//import com.google.gson.annotations.SerializedName
//import java.time.ZonedDateTime
//
//enum class TypeOfEvent {
//    VOD, CUTV, CHANNEL, SERIE
//}
//data class ContentEntity (
//    @SerializedName("id")
//    var id: Int? = null,
//
//    @SerializedName("idPpal", alternate = ["id_event", "id_content"])
//    var idPpal: String? = null,
//
//    @SerializedName("channelById", alternate = ["id_channel", "channelId"])
//    var channelById: String? = null,
//
//    @SerializedName("title", alternate = ["name"])
//    var title: String? = null,
//
//    @SerializedName("subtitle")
//    var subtitle: String? = null,
//
//    @SerializedName("description", alternate = ["shortDesc"])
//    var description: String? = null,
//
//    @SerializedName("fcIni", alternate = ["fc_ini", "startDate"])
//    var fcIni: String? = null,
//
//    @SerializedName("fcEnd", alternate = ["fc_end", "expiryDate"])
//    var fcEnd: String? = null,
//
//    @SerializedName("duration")
//    var duration: String? = null,
//
//    @SerializedName("logoURL", alternate = ["serie_logo_url", "event_logo_url", "content_logo"])
//    var logoURL: String? = null,
//
//    @SerializedName("parental")
//    var parental: String? = null,
//
//    @SerializedName("subgenreById", alternate = ["id_subgenre"])
//    var subgenreById: String? = null,
//
//    @SerializedName("featured")
//    var featured: Int? = null,
//
//    @SerializedName("items")
//    var items: String? = null,
//
//    @SerializedName("deliveryURL", alternate = ["path"])
//    var deliveryURL: String? = null,
//
//    @SerializedName("tbEventLanguages", alternate = ["tbContentLanguages"])
//    var titleArray: ArrayList<TbEventLanguage> = arrayListOf(),
//
//    @SerializedName("idContent")
//    var titleArray2: ArrayList<TbEventLanguage> = arrayListOf(),
//
//    @SerializedName("seasons")
//    var seasonList: List<Season> = listOf(),
//
//    var fcIniDate: ZonedDateTime = ZonedDateTime.now(),
//    var fcEndDate: ZonedDateTime = ZonedDateTime.now(),
//    var kind: TypeOfEvent? = null,
//
//    // For search
//    @SerializedName("type")
//    var searchType: String? = null,
//
//    @SerializedName("rmatch")
//    var rmatch: Double = 0.0
//
//) {
//
//    fun hasEnded(): Boolean {
//        return ZonedDateTime.now() > this.fcEndDate
//    }
//
//    fun isLive(): Boolean {
//        return ZonedDateTime.now() < this.fcEndDate && ZonedDateTime.now() > this.fcIniDate
//    }
//}
//
//data class TbEventLanguage(
//    @SerializedName("title")
//    var title: String = "",
//
//    @SerializedName("description", alternate = ["short_description"])
//    var description: String = "",
//
//    @SerializedName("tbContentSeasons")
//    var chapters: List<ContentEntity> = listOf()
//)
//
