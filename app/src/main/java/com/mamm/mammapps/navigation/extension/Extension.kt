package com.mamm.mammapps.navigation.extension

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.screen.HomeScreen

fun SavedStateHandle.addContentUI(content: ContentEntityUI) {
    this["contentUI"] = content
}

fun SavedStateHandle.removeContentUI() {
    this.remove<ContentEntityUI>("contentUI")
}

fun SavedStateHandle.addContentClass(content: Any) {
    this["contentClass"] = content
}

fun SavedStateHandle.retrieveContentUI(): ContentEntityUI? {
    return this.get<ContentEntityUI>("contentUI")
}

fun SavedStateHandle.retrieveContentClass(): Any? {
    return this.get<Any>("contentClass")
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
                    addContentUI(content)
                    addRoute(appRoute)
                }
            },
            onPlay = {}
        )
    }
}