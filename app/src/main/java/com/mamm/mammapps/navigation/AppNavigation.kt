package com.mamm.mammapps.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.EPGEvent
import com.mamm.mammapps.data.model.Event
import com.mamm.mammapps.data.model.VoD
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.mapper.toContentEntityUI
import com.mamm.mammapps.ui.screen.VideoPlayerScreen
import com.mamm.mammapps.ui.mapper.toContentToPlayUI
import com.mamm.mammapps.ui.model.AppRoute
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

@Composable
fun TVNavigationLayout(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val sectionsWithMenu = listOf(
        AppRoute.HOME.route,
        AppRoute.EPG.route,
        AppRoute.MOVIES.route,
        AppRoute.ADULTS.route,
        AppRoute.DOCUMENTARIES.route,
        AppRoute.SERIES.route,
        AppRoute.SPORTS.route
    )
    val showNavigationRail = currentRoute in sectionsWithMenu

    Row {
        AnimatedVisibility(
            visible = showNavigationRail,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        ) {
            NavigationRail(
                modifier = Modifier.width(80.dp)
            ) {
                NavigationRailItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = { navController.navigate("home") }
                )
                NavigationRailItem(
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("EPG") },
                    selected = false,
                    onClick = { navController.navigate("epg") }
                )
                NavigationRailItem(
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Cine") },
                    selected = false,
                    onClick = { navController.navigate("movies") }
                )
            }
        }

        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.fillMaxSize()
        ) {
            navigationGraph(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileNavigationLayout(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = {
                        navController.navigate("home")
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = {
                        navController.navigate("profile")
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Column {
            TopAppBar(
                title = { Text("Mi App") },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, null)
                    }
                }
            )

            NavHost(
                navController = navController,
                startDestination = AppRoute.LOGIN.route,
                modifier = Modifier.fillMaxSize()
            ) {
                navigationGraph(navController)
            }
        }
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
                when (content) {
                    is VoD,
                    is Event -> {
//                        navController.navigate(AppRoute.DETAIL.route) {
//                            launchSingleTop = true
//                        }
                        navController.navigate(AppRoute.PLAYER.route) {
                            launchSingleTop = true
                        }
                    }

                    is Channel -> {
                        navController.navigate(AppRoute.PLAYER.route) {
                            launchSingleTop = true
                        }
                    }
                }
                navController.currentBackStackEntry?.savedStateHandle?.set("content", content)
            }
        )
    }

    composable(AppRoute.MOVIES.route) {
        HomeScreen(
            routeTag = AppRoute.MOVIES,
            onContentClicked = { content ->
                navController.navigate(AppRoute.DETAIL.route) {
                    launchSingleTop = true
                }
                navController.currentBackStackEntry?.savedStateHandle?.set("content", content)
            }
        )
    }

    composable(AppRoute.DETAIL.route) { backStackEntry ->
        val contentItem = remember(backStackEntry) {
            backStackEntry.savedStateHandle.get<Any>("content")
        }

        contentItem?.let {
            DetailScreen(
                content = when (it) {
                    is VoD -> it.toContentEntityUI()
                    is Event -> it.toContentEntityUI()
                    is EPGEvent -> it.toContentEntityUI()
                    else -> return@composable
                },
                onPlayClick = { TODO()
                }
            )
        } ?: Text("No content available")
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
                    else -> return@composable
                }
            )
        }
    }


}