package com.mamm.mammapps.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.data.model.Event
import com.mamm.mammapps.data.model.Serie
import com.mamm.mammapps.data.model.VoD
import com.mamm.mammapps.data.model.branded.BrandedVod
import com.mamm.mammapps.data.model.section.SectionVod
import com.mamm.mammapps.navigation.extension.addContent
import com.mamm.mammapps.navigation.extension.addRoute
import com.mamm.mammapps.navigation.extension.homeScreenRoute
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.mapper.toContentEntityUI
import com.mamm.mammapps.ui.screen.VideoPlayerScreen
import com.mamm.mammapps.ui.mapper.toContentToPlayUI
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.screen.ChannelsScreen
import com.mamm.mammapps.ui.screen.DetailScreen
import com.mamm.mammapps.ui.screen.EPGScreen
import com.mamm.mammapps.ui.screen.HomeScreen
import com.mamm.mammapps.ui.screen.LoginScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    if (LocalIsTV.current) {
        TVNavigationLayout(navController)
    } else {
        MobileNavigationLayout(navController)
//        TVNavigationLayout(navController)
    }
}

fun NavGraphBuilder.navigationGraph(navController: NavHostController) {
    composable(AppRoute.LOGIN.route) {
        LoginScreen(
            onNavigateToHome = {
                navController.navigate(AppRoute.HOME.route) {
                    popUpTo(AppRoute.LOGIN.route) { inclusive = true }
                }
            }
        )
    }

    composable(AppRoute.HOME.route) {
        HomeScreen(
            onContentClicked = { content ->
                when (content.identifier) {
                    is ContentIdentifier.Channel -> {
                        navController.navigate(AppRoute.PLAYER.route) {
                            launchSingleTop = true
                        }
                    }
                    else -> {
                        navController.navigate(AppRoute.DETAIL.route) {
                            launchSingleTop = true
                        }
                    }
                }
                navController.currentBackStackEntry?.savedStateHandle?.addContent(content)
                navController.currentBackStackEntry?.savedStateHandle?.addRoute(AppRoute.HOME)
            }
        )
    }

    composable(AppRoute.CHANNELS.route) {
        ChannelsScreen(
            onContentClicked = { content ->
                navController.navigate(AppRoute.PLAYER.route) {
                    launchSingleTop = true
                }
                navController.currentBackStackEntry?.savedStateHandle?.addContent(content)
            }
        )
    }

    homeScreenRoute(AppRoute.SERIES, navController)
    homeScreenRoute(AppRoute.MOVIES, navController)
    homeScreenRoute(AppRoute.DOCUMENTARIES, navController)
    homeScreenRoute(AppRoute.KIDS, navController)
    homeScreenRoute(AppRoute.SPORTS, navController)
    homeScreenRoute(AppRoute.WARNER, navController)
    homeScreenRoute(AppRoute.ACONTRA, navController)
    homeScreenRoute(AppRoute.AMC, navController)
    homeScreenRoute(AppRoute.ADULTS, navController)


    composable(AppRoute.DETAIL.route) { backStackEntry ->
        val contentItem = remember(backStackEntry) {
            backStackEntry.savedStateHandle.get<Any>("content")
        }

        val route = remember(backStackEntry) {
            backStackEntry.savedStateHandle.get<AppRoute>("route")
        }

        if (contentItem != null && route != null) {
            DetailScreen(
                content = when (contentItem) {
                    is VoD -> contentItem.toContentEntityUI()
                    is Event -> contentItem.toContentEntityUI()
                    is EPGEvent -> contentItem.toContentEntityUI()
                    is Serie -> contentItem.toContentEntityUI()
                    is BrandedVod -> contentItem.toContentEntityUI()
                    else -> return@composable // Or handle error appropriately
                },
                onClickPlay = { content ->
                    navController.navigate(AppRoute.PLAYER.route) {
                        launchSingleTop = true
                    }
                    navController.currentBackStackEntry?.savedStateHandle?.addContent(content)
                },
                routeTag = route
            )
        } else {
            Text("No content available or route is missing")
        }
    }

    composable(AppRoute.EPG.route) {
        EPGScreen()
    }

    composable(AppRoute.PLAYER.route) { backStackEntry ->
        val contentItem = remember(backStackEntry) {
            backStackEntry.savedStateHandle.get<Any>("content")
        }

        contentItem?.let {
            VideoPlayerScreen(
                playedContent = when (it) {
                    is VoD -> it.toContentToPlayUI()
                    is Event -> it.toContentToPlayUI()
                    is Channel -> it.toContentToPlayUI()
                    is EPGEvent -> it.toContentToPlayUI()
                    is SectionVod -> it.toContentToPlayUI()
                    else -> return@composable
                }
            )
        }
    }


}