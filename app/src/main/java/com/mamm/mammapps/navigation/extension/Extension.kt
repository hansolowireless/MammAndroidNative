package com.mamm.mammapps.navigation.extension

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.screen.HomeScreen

fun SavedStateHandle.addContent(content: ContentEntityUI) {
    this["content"] = content
}

fun SavedStateHandle.addContentToPlay(content: Any) {
    this["content"] = content
}

fun SavedStateHandle.addRoute(route: AppRoute) {
    this["route"] = route
}

fun NavGraphBuilder.homeScreenRoute(
    appRoute: AppRoute,
    navController: NavController
) {
    composable(appRoute.route) {
        HomeScreen(
            routeTag = appRoute,
            onShowDetails = { content ->
                navController.navigate(AppRoute.DETAIL.route) {
                    launchSingleTop = true
                }
                navController.currentBackStackEntry?.savedStateHandle?.apply {
                    addContent(content)
                    addRoute(appRoute)
                }
            },
            onPlay = {}
        )
    }
}