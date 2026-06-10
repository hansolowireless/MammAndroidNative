package com.mamm.mammapps.domain.model.entity

import android.os.Parcelable
import com.mamm.mammapps.domain.model.LogoTransition
import com.mamm.mammapps.domain.model.metadata.Metadata
import com.mamm.mammapps.util.getCurrentDate
import com.mamm.mammapps.util.toZonedDateTimeEPG
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class Event(
    val id: Int? = null,
    val title: String = "",
    val description: String = "",
    val subtitle: String = "",
    val duration: Int? = null,
    val deliveryURL: String? = null,
    val logoURL: String? = null,
    val posterLogo: String? = null,
    val eventLogoUrl500: String? = null,
    val eventLogoUrl: String? = null,
    val channelById: Int? = null,
    val subgenreById: Int? = null,
    val logoTransitions: List<LogoTransition>? = null,
    val metadata: Metadata? = null,
    val fcIni: String? = null,
    val fcEnd: String? = null,
    val parental: Int? = null
) : Parcelable {

    fun getId(): Int = id ?: 0

    fun getChannelId(): Int = channelById ?: 0

    val startDateTime: ZonedDateTime?
        get() = fcIni?.toZonedDateTimeEPG()

    val endDateTime: ZonedDateTime?
        get() = fcEnd?.toZonedDateTimeEPG()

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
}
