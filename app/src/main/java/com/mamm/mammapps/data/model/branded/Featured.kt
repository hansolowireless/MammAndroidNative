package com.mamm.mammapps.data.model.branded

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Featured(
    @SerializedName("subgenreById")
    val subgenreById: String? = null,

    @SerializedName("featured")
    val featured: Int? = null,

    @SerializedName("urlLoop")
    val urlLoop: String? = null,

    @SerializedName("logoTransitions")
    val logoTransitions: List<LogoTransition>? = null,

    @SerializedName("format")
    val format: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("fcIni")
    val fcIni: String? = null,

    @SerializedName("logoURL")
    val logoUrl: String? = null,

    @SerializedName("duration")
    val duration: String? = null,

    @SerializedName("idPpal")
    val idPpal: String? = null,

    @SerializedName("urlLoopMpd")
    val urlLoopMpd: String? = null,

    @SerializedName("formatid")
    val formatId: String? = null,

    @SerializedName("fcEnd")
    val fcEnd: String? = null,

    @SerializedName("subtitle")
    val subtitle: String? = null,

    @SerializedName("channelById")
    val channelById: Int? = null,

    @SerializedName("deliveryURL")
    val deliveryUrl: String? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("items")
    val items: String? = null,

    @SerializedName("parental")
    val parental: String? = null
) : Parcelable