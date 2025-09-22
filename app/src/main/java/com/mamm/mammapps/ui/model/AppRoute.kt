package com.mamm.mammapps.ui.model

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
    DIAGNOSTICS,
    PLAYER,
    DETAIL,
    LOGIN;

    val route: String
        get() = name.lowercase()
}