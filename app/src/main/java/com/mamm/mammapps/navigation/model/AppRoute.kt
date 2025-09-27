package com.mamm.mammapps.navigation.model

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
    DIAGNOSTICS,
    PLAYER,
    DETAIL,
    LOGIN,
    LOGOUT;

    val route: String
        get() = name.lowercase()
}