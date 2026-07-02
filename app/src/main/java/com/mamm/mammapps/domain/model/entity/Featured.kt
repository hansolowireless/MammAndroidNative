package com.mamm.mammapps.domain.model.entity

import android.os.Parcelable
import com.mamm.mammapps.domain.model.LogoTransition
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class Featured(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val type: FeaturedFormat = FeaturedFormat.UNKNOWN,
    val logoURL: String? = null,
    val logoUrl3x1: String? = null,
    val deliveryURL: String? = null,
    val channelById: Int? = null,
    val logoTransitions: List<LogoTransition>? = null,
    val subgenreById: Int? = null,
    val duration: Int? = null,
    val fcIni: String? = null,
    val fcEnd: String? = null
) : Parcelable {

    fun isValidByDate(): Boolean {
        // If no dates provided (CMS featured), always valid
        if (fcIni == null && fcEnd == null) return true

        return try {
            // Try both formats: operator format "yyyy-MM-dd HH:mm:ss" and CMS format "yyyy-MM-dd'T'HH:mm:ss'Z'"
            val formatters = listOf(
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            )
            val now = Date()

            var startDate: Date? = null
            var endDate: Date? = null

            // Try parsing with each formatter
            for (formatter in formatters) {
                if (startDate == null && fcIni != null) {
                    try { startDate = formatter.parse(fcIni) } catch (e: Exception) { }
                }
                if (endDate == null && fcEnd != null) {
                    try { endDate = formatter.parse(fcEnd) } catch (e: Exception) { }
                }
            }

            when {
                startDate != null && endDate != null -> now.after(startDate) && now.before(endDate)
                startDate != null -> now.after(startDate)
                endDate != null -> now.before(endDate)
                else -> true
            }
        } catch (e: Exception) {
            false
        }
    }
}
