package com.mamm.mammapps.navigation.model

enum class AppRoute {
    HOME,
    CHANNELS,
    MOVIES,
    DOCUMENTARIES,
    SERIES,
    SPORTS,
    KIDS,
    SEARCH,
    ADULTS,
    EPG,
    LASTSEVENDAYS,
    DIAGNOSTICS,
    PLAYER,
    DETAIL,
    LOGIN,
    LOGOUT;

    val route: String
        get() = name.lowercase()
}