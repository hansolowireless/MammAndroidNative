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
    EXPANDCATEGORY,
    LOGIN,
    LOGOUT,
    ERROR_SUBSCRIPTION_ONGOING;

    val route: String
        get() = name.lowercase()
}