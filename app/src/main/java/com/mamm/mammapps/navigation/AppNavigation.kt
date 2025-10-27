package com.mamm.mammapps.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mamm.mammapps.navigation.extension.addCategoryId
import com.mamm.mammapps.navigation.extension.addCategoryName
import com.mamm.mammapps.navigation.extension.addContentClass
import com.mamm.mammapps.navigation.extension.addContentUI
import com.mamm.mammapps.navigation.extension.addRoute
import com.mamm.mammapps.navigation.extension.homeScreenRoute
import com.mamm.mammapps.navigation.extension.removeContentUI
import com.mamm.mammapps.navigation.extension.retrieveCategoryId
import com.mamm.mammapps.navigation.extension.retrieveCategoryName
import com.mamm.mammapps.navigation.extension.retrieveContentClass
import com.mamm.mammapps.navigation.extension.retrieveContentUI
import com.mamm.mammapps.navigation.extension.retrieveRoute
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.mapper.toContentEntityUI
import com.mamm.mammapps.ui.mapper.toContentToPlayUI
import com.mamm.mammapps.ui.screen.ChannelsScreen
import com.mamm.mammapps.ui.screen.DetailScreen
import com.mamm.mammapps.ui.screen.EPGScreen
import com.mamm.mammapps.ui.screen.ExpandCategoryScreen
import com.mamm.mammapps.ui.screen.HomeScreen
import com.mamm.mammapps.ui.screen.LoginScreen
import com.mamm.mammapps.ui.screen.LogoutScreen
import com.mamm.mammapps.ui.screen.SearchScreen
import com.mamm.mammapps.ui.screen.VideoPlayerScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    if (LocalIsTV.current) {
        TVNavigationLayout(navController)
    } else {
        MobileNavigationLayout(navController)
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
                navController.currentBackStackEntry?.savedStateHandle?.addContentUI(content)
                navController.currentBackStackEntry?.savedStateHandle?.addRoute(AppRoute.HOME)
            },
            onPlay = { content ->
                navController.navigate(AppRoute.PLAYER.route) {
                    launchSingleTop = true
                }
                navController.currentBackStackEntry?.savedStateHandle?.addContentClass(content)
            },
            onExpandCategory = { categoryId, categoryName ->
                navController.navigate(AppRoute.EXPANDCATEGORY.route) {
                    launchSingleTop = true
                }
                navController.currentBackStackEntry?.savedStateHandle?.addCategoryId(categoryId)
                navController.currentBackStackEntry?.savedStateHandle?.addCategoryName(categoryName)
                navController.currentBackStackEntry?.savedStateHandle?.addRoute(AppRoute.HOME)
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

    composable(AppRoute.CHANNELS.route) {
        ChannelsScreen(
            onContentClicked = { content ->
                navController.navigate(AppRoute.PLAYER.route) {
                    launchSingleTop = true
                }
                navController.currentBackStackEntry?.savedStateHandle?.addContentClass(content)
            }
        )
    }

    composable(AppRoute.EPG.route) {
        EPGScreen(
            onShowDetails = { content ->
                navController.navigate(AppRoute.DETAIL.route) {
                    launchSingleTop = true
                }
                navController.currentBackStackEntry?.savedStateHandle?.addRoute(AppRoute.EPG)
                navController.currentBackStackEntry?.savedStateHandle?.addContentClass(content)
            },
            onPlayClick = { content ->
                navController.navigate(AppRoute.PLAYER.route) {
                    launchSingleTop = true
                }
                navController.currentBackStackEntry?.savedStateHandle?.addRoute(AppRoute.EPG)
                navController.currentBackStackEntry?.savedStateHandle?.addContentClass(content)
            }
        )
    }

    composable(AppRoute.SEARCH.route) {
        SearchScreen(onContentClick = {
            navController.navigate(AppRoute.DETAIL.route) {
                launchSingleTop = true
            }
            navController.currentBackStackEntry?.savedStateHandle?.addRoute(AppRoute.SEARCH)
            navController.currentBackStackEntry?.savedStateHandle?.addContentClass(it)
        })
    }

    composable(AppRoute.DETAIL.route) { backStackEntry ->
        //Comes from EPG or from a click in "Similar Content" in the detail view
        val prefoundContent = remember(backStackEntry) {
            backStackEntry.savedStateHandle.retrieveContentClass()
        }

        //El Any.toContentEntityUI (prefound.toContentEntityUI) no me convence mucho, supuestamente coge el que más sentido tenga por el polimorfismo de Kotlin
        val contentUI = remember(backStackEntry) {
            backStackEntry.savedStateHandle.retrieveContentUI()
        } ?: prefoundContent?.toContentEntityUI()

        val route = remember(backStackEntry) {
            backStackEntry.savedStateHandle.get<AppRoute>("route")
        }

        if (contentUI != null && route != null) {
            DetailScreen(
                content = contentUI,
                prefoundContent = prefoundContent,
                onClickPlay = { content ->
                    navController.navigate(AppRoute.PLAYER.route) {
                        launchSingleTop = true
                    }
                    navController.currentBackStackEntry?.savedStateHandle?.addContentClass(content)
                },
                onSimilarContentClick = { content ->

                    //Quitamos el contentUI para que la siguiente vista Detalle no coja el que tenía de antes
                    navController.currentBackStackEntry?.savedStateHandle?.removeContentUI()
                    navController.currentBackStackEntry?.savedStateHandle?.addContentClass(content)
                    navController.currentBackStackEntry?.savedStateHandle?.addRoute(route)
                    navController.navigate(AppRoute.DETAIL.route) {
                        launchSingleTop = true
                    }
                },
                routeTag = route
            )
        } else {
            Text("No content available or route is missing")
        }
    }

    composable(AppRoute.PLAYER.route) { backStackEntry ->
        val contentItem = remember(backStackEntry) {
            backStackEntry.savedStateHandle.retrieveContentClass()
        }

        contentItem?.toContentToPlayUI()?.let {
            VideoPlayerScreen(
                playedContent = it
            )
        }
    }

    composable(AppRoute.EXPANDCATEGORY.route) {
        val categoryId = remember(it) {
            it.savedStateHandle.retrieveCategoryId()
        }

        val categoryName = remember(it) {
            it.savedStateHandle.retrieveCategoryName()
        }

        val routeName = remember(it) {
            it.savedStateHandle.retrieveRoute()
        }

        ExpandCategoryScreen(
            categoryId = categoryId,
            categoryName = categoryName,
            onContentClick = { content ->
                navController.navigate(AppRoute.DETAIL.route) {
                    launchSingleTop = true
                }
                navController.currentBackStackEntry?.savedStateHandle?.addRoute(AppRoute.EXPANDCATEGORY)
                navController.currentBackStackEntry?.savedStateHandle?.addContentClass(content)
            }
        )
    }

    composable(AppRoute.LOGOUT.route) {
        LogoutScreen(
            onNavigateToLogin = {
                navController.navigate(AppRoute.LOGIN.route) {
                    popUpTo(AppRoute.LOGOUT.route) { inclusive = true }
                }
            }
        )
    }


}