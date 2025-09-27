package com.mamm.mammapps.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.Event
import com.mamm.mammapps.data.model.VoD
import com.mamm.mammapps.data.model.branded.BrandedVod
import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.data.model.section.SectionVod
import com.mamm.mammapps.navigation.extension.addContent
import com.mamm.mammapps.navigation.extension.addContentToPlay
import com.mamm.mammapps.navigation.extension.addRoute
import com.mamm.mammapps.navigation.extension.homeScreenRoute
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.mapper.toContentToPlayUI
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.screen.ChannelsScreen
import com.mamm.mammapps.ui.screen.DetailScreen
import com.mamm.mammapps.ui.screen.EPGScreen
import com.mamm.mammapps.ui.screen.HomeScreen
import com.mamm.mammapps.ui.screen.LoginScreen
import com.mamm.mammapps.ui.screen.VideoPlayerScreen

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
            onShowDetails = { content ->
                navController.navigate(AppRoute.DETAIL.route) {
                    launchSingleTop = true
                }
                navController.currentBackStackEntry?.savedStateHandle?.addContent(content)
                navController.currentBackStackEntry?.savedStateHandle?.addRoute(AppRoute.HOME)
            },
            onPlay = { content ->
                navController.navigate(AppRoute.PLAYER.route) {
                    launchSingleTop = true
                }
                navController.currentBackStackEntry?.savedStateHandle?.addContentToPlay(content)
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
                navController.currentBackStackEntry?.savedStateHandle?.addContentToPlay(content)
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
            backStackEntry.savedStateHandle.get<ContentEntityUI>("content")
        }

        val route = remember(backStackEntry) {
            backStackEntry.savedStateHandle.get<AppRoute>("route")
        }

        if (contentItem != null && route != null) {
            DetailScreen(
                content = contentItem,
                onClickPlay = { content ->
                    navController.navigate(AppRoute.PLAYER.route) {
                        launchSingleTop = true
                    }
                    navController.currentBackStackEntry?.savedStateHandle?.addContentToPlay(content)
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

        val playableContent = when (contentItem) {
            is Channel -> contentItem.toContentToPlayUI()
            is VoD -> contentItem.toContentToPlayUI()
            is Event -> contentItem.toContentToPlayUI()
            is EPGEvent -> contentItem.toContentToPlayUI()
            is SectionVod -> contentItem.toContentToPlayUI()
            is BrandedVod -> contentItem.toContentToPlayUI()
            else -> null
        }

        playableContent?.let {
            VideoPlayerScreen(
                playedContent = playableContent
            )
        }
    }


}