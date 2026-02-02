package com.mamm.mammapps.navigation.model

import androidx.annotation.StringRes
import com.mamm.mammapps.R

enum class AppRoute {
    HOME,
    EPG,
    CHANNELS,
    MOVIES,
    DOCUMENTARIES,
    SERIES,
    SPORTS,
    KIDS,
    WARNER,
    ACONTRA,
    AMC,
    SEARCH,
    ADULTS,
    LASTSEVENDAYS,
    MEMORIES,
    ABOUT,
    PLAYER,
    DETAIL,
    EXPANDCATEGORY,
    LOGIN,
    LOGOUT,
    EXIT,
    ERROR_SUBSCRIPTION_ONGOING;

    companion object {
        /**
         * Obtiene una instancia de AppRoute a partir de su valor de ruta en minúsculas.
         * Devuelve null si no se encuentra ninguna coincidencia.
         *
         * @param route La cadena de la ruta (p. ej., "home", "epg").
         * @return El AppRoute correspondiente o null.
         */
        fun fromRoute(route: String?): AppRoute? {
            return try {
                valueOf(route?.uppercase() ?: return null)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    val route: String
        get() = name.lowercase()

    @StringRes
    fun getResId(): Int {
        return when (this) {
            HOME -> R.string.nav_home
            EPG -> R.string.nav_epg
            CHANNELS -> R.string.nav_channels
            MOVIES -> R.string.nav_movies
            DOCUMENTARIES -> R.string.nav_documentaries
            SERIES -> R.string.nav_series
            WARNER -> R.string.nav_warner
            ACONTRA -> R.string.nav_acontra
            AMC -> R.string.nav_amc
            SPORTS -> R.string.nav_sports
            MEMORIES -> R.string.nav_my_memories
            ADULTS -> R.string.nav_adults
            KIDS -> R.string.nav_kids
            SEARCH -> R.string.nav_search
            ABOUT -> R.string.nav_diagnostics
            LOGOUT -> R.string.nav_change_user
            EXIT -> R.string.nav_exit
            else -> R.string.app_name
        }
    }
}