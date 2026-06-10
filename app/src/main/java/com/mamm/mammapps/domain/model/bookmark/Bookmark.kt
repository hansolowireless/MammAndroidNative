package com.mamm.mammapps.domain.model.bookmark

import android.os.Parcelable
import com.mamm.mammapps.domain.model.metadata.Metadata
import com.mamm.mammapps.util.toZonedDateTimeEPG
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

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
    abstract val metadata: Metadata?

    // Propiedades calculadas
    val startDateTime: ZonedDateTime?
        get() = fcIni?.toZonedDateTimeEPG()

    val endDateTime: ZonedDateTime?
        get() = fcEnd?.toZonedDateTimeEPG()

}

@Parcelize
data class Bookmark(
    override val type: String? = null,
    override val id: Int? = null,
    override val title: String? = null,
    override val shortDesc: String? = null,
    override val longDesc: String? = null,
    override val duration: String? = null,
    override val startDate: String? = null,
    override val expiryDate: String? = null,
    override val logoURL: String? = null,
    override val posterLogo: String? = null,
    override val deliveryURL: String? = null,
    override val channelId: String? = null,
    override val currentTime: Int? = null,
    override val fcStored: String? = null,
    override val subgenreById: Int? = null,
    override val fcIni: String? = null,
    override val fcEnd: String? = null,
    override val metadata: Metadata? = null
) : BookmarkContent()
