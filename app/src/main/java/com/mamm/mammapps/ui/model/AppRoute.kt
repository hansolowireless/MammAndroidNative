package com.mamm.mammapps.ui.model

enum class AppRoute {
    HOME,
    MOVIES,
    DOCUMENTARIES,
    SERIES,
    SPORTS,
    ADULTS,
    EPG,
    PLAYER,
    DETAIL,
    LOGIN;

    val route: String
        get() = name.lowercase()
}