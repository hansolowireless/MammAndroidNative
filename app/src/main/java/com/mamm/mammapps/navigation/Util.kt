package com.mamm.mammapps.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.BuildConfig
import com.mamm.mammapps.R
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.navigation.model.AppRoute.LOGOUT
import com.mamm.mammapps.ui.component.icon.BulletedList
import com.mamm.mammapps.ui.component.icon.Fire
import com.mamm.mammapps.ui.component.icon.Memories
import com.mamm.mammapps.ui.component.icon.Puzzle
import com.mamm.mammapps.ui.component.icon.WifiSignal


object MenuItems {
    @Composable
    fun GetIconForRoute(route: AppRoute) {
        val iconModifier = Modifier.size(24.dp)
        when (route) {
            AppRoute.HOME -> Icon(
                Icons.Default.Home,
                contentDescription = null,
                modifier = iconModifier
            )

            AppRoute.EPG -> Icon(
                BulletedList,
                contentDescription = null,
                modifier = iconModifier
            )

            AppRoute.CHANNELS -> Icon(
                painterResource(id = R.drawable.menu_remoteicon),
                contentDescription = null,
                modifier = iconModifier
            )

            AppRoute.MOVIES -> Icon(
                painterResource(id = R.drawable.menu_cinemaicon2),
                contentDescription = null,
                modifier = iconModifier
            )

            AppRoute.DOCUMENTARIES -> Icon(
                painterResource(id = R.drawable.menu_documentariesicon),
                contentDescription = null,
                modifier = iconModifier
            )

            AppRoute.SERIES -> Icon(
                painterResource(id = R.drawable.menu_serieslogoicon),
                contentDescription = null,
                modifier = iconModifier
            )

            AppRoute.WARNER -> Icon(
                painterResource(id = R.drawable.menu_wblogoicon),
                contentDescription = null,
                modifier = iconModifier
            )

            AppRoute.ACONTRA -> Icon(
                painterResource(id = R.drawable.menu_acontralogoicon),
                contentDescription = null,
                modifier = iconModifier
            )

            AppRoute.AMC -> Icon(
                painterResource(id = R.drawable.menu_amclogoicon),
                contentDescription = null,
                modifier = iconModifier
            )

            AppRoute.SPORTS -> Icon(
                painterResource(id = R.drawable.menu_iconfootball),
                contentDescription = null,
                modifier = iconModifier
            )

            AppRoute.MEMORIES -> Icon(
                imageVector = Memories,
                contentDescription = null,
                modifier = iconModifier
            )

            AppRoute.ADULTS -> Icon(
                Fire,
                contentDescription = null,
                modifier = iconModifier
            )

            AppRoute.KIDS -> Icon(
                Puzzle,
                contentDescription = null,
                modifier = iconModifier
            )

            AppRoute.SEARCH -> Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier = iconModifier
            )

            AppRoute.ABOUT -> Icon(WifiSignal, contentDescription = null, modifier = iconModifier)
            AppRoute.LOGOUT -> Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = iconModifier
            )

            AppRoute.EXIT -> Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = iconModifier
            )

            else -> {} // Para otras rutas como Login, etc.
        }
    }

    private val masterList = listOf(
        AppRoute.HOME,
        AppRoute.EPG,
        AppRoute.CHANNELS,
        AppRoute.MOVIES,
        AppRoute.DOCUMENTARIES,
        AppRoute.SPORTS,
        AppRoute.KIDS,
        AppRoute.SERIES,
        AppRoute.WARNER,
        AppRoute.ACONTRA,
        AppRoute.AMC,
        AppRoute.MEMORIES,
        AppRoute.ADULTS,
        AppRoute.SEARCH,
        AppRoute.ABOUT,
        AppRoute.LOGOUT,
        AppRoute.EXIT
    )

    val list: List<AppRoute> = when (BuildConfig.FLAVOR) {
        "eligetv" -> {
            masterList.filter { it != AppRoute.AMC }
        }

        "fibrazo" -> {
            masterList.filter {
                it != AppRoute.MOVIES &&
                        it != AppRoute.DOCUMENTARIES &&
                        it != AppRoute.SPORTS &&
                        it != AppRoute.KIDS &&
                        it != AppRoute.SERIES &&
                        it != AppRoute.WARNER &&
                        it != AppRoute.ACONTRA &&
                        it != AppRoute.AMC &&
                        it != AppRoute.SEARCH
            }
        }

        else -> {
            masterList
        }
    }

    val listNoSpanishUserContent = list.filter {
        it != AppRoute.WARNER
                && it != AppRoute.AMC
                && it != AppRoute.ACONTRA
                && it != AppRoute.MEMORIES
    }

    fun showSideMenu(route: AppRoute): Boolean {
        return when (route) {
            LOGOUT,
            AppRoute.PLAYER,
            AppRoute.DETAIL,
            AppRoute.EXPANDCATEGORY,
            AppRoute.LOGIN,
            AppRoute.EXIT,
            AppRoute.ERROR_SUBSCRIPTION_ONGOING -> false

            else -> true
        }
    }

    fun showLogoOnMenu(): Boolean {
        return when (BuildConfig.FLAVOR) {
            "fibrazo" -> {
                true
            }

            else -> {
                false
            }
        }
    }
}
