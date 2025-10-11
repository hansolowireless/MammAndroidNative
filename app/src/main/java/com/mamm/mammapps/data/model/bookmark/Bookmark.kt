package com.mamm.mammapps.data.model.bookmark

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mamm.mammapps.data.extension.toZonedDateTimeEPG
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

/**
 * Define una jerarquía sellada para contenido que puede ser marcado o recomendado.
 * Todas las propiedades comunes se declaran aquí como `abstract`.
 * Al ser `Parcelable`, toda la jerarquía puede pasarse entre componentes de Android.
 */
sealed class BookmarkContent : Parcelable {
    abstract val type: String?
    abstract val id: Int?
    abstract val title: String?
    abstract val shortDesc: String?
    abstract val longDesc: String?
    abstract val duration: String?
    abstract val startDate: String?
    abstract val expiryDate: String?
    abstract val logoURL: String?
    abstract val posterLogo: String?
    abstract val deliveryURL: String?
    abstract val channelId: String?
    abstract val currentTime: Int?
    abstract val fcStored: String?
    abstract val subgenreById: Int?
    abstract val fcIni: String?
    abstract val fcEnd: String?

    // Propiedades calculadas que se heredan automáticamente
    val startDateTime: ZonedDateTime?
        get() = fcIni?.toZonedDateTimeEPG()

    val endDateTime: ZonedDateTime?
        get() = fcEnd?.toZonedDateTimeEPG()
}

/**
 * Representa un marcador de usuario. Hereda de `BookmarkContent`.
 */
@Parcelize
data class Bookmark(
    @SerializedName("type") override val type: String? = null,
    @SerializedName("id") override val id: Int? = null,
    @SerializedName("title") override val title: String? = null,
    @SerializedName("shortDesc") override val shortDesc: String? = null,
    @SerializedName("longDesc") override val longDesc: String? = null,
    @SerializedName("duration") override val duration: String? = null,
    @SerializedName("startDate") override val startDate: String? = null,
    @SerializedName("expiryDate") override val expiryDate: String? = null,
    @SerializedName("logoURL") override val logoURL: String? = null,
    @SerializedName("poster_logo") override val posterLogo: String? = null,
    @SerializedName("deliveryURL") override val deliveryURL: String? = null,
    @SerializedName("channelId") override val channelId: String? = null,
    @SerializedName("currentTime") override val currentTime: Int? = null,
    @SerializedName("fcStored") override val fcStored: String? = null,
    @SerializedName("subgenreById") override val subgenreById: Int? = null,
    @SerializedName("fcIni") override val fcIni: String? = null,
    @SerializedName("fcEnd") override val fcEnd: String? = null
) : BookmarkContent()


/**
 * Representa un contenido recomendado. Tiene las mismas propiedades que Bookmark,
 * pero es un tipo diferente para poder distinguirlo.
 */
@Parcelize
data class Recommended(
    @SerializedName("type") override val type: String? = null,
    @SerializedName("id") override val id: Int? = null,
    @SerializedName("title") override val title: String? = null,
    @SerializedName("shortDesc") override val shortDesc: String? = null,
    @SerializedName("longDesc") override val longDesc: String? = null,
    @SerializedName("duration") override val duration: String? = null,
    @SerializedName("startDate") override val startDate: String? = null,
    @SerializedName("expiryDate") override val expiryDate: String? = null,
    @SerializedName("logoURL") override val logoURL: String? = null,
    @SerializedName("poster_logo") override val posterLogo: String? = null,
    @SerializedName("deliveryURL") override val deliveryURL: String? = null,
    @SerializedName("channelId") override val channelId: String? = null,
    @SerializedName("currentTime") override val currentTime: Int? = null,
    @SerializedName("fcStored") override val fcStored: String? = null,
    @SerializedName("subgenreById") override val subgenreById: Int? = null,
    @SerializedName("fcIni") override val fcIni: String? = null,
    @SerializedName("fcEnd") override val fcEnd: String? = null
) : BookmarkContent()
