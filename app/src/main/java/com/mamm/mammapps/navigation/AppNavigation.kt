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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.section.EPGEvent
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
        AppRoute.CHANNELS.route,
        AppRoute.MOVIES.route,
        AppRoute.DOCUMENTARIES.route,
        AppRoute.KIDS.route,
        AppRoute.SERIES.route,
        AppRoute.SPORTS.route,
        AppRoute.ADULTS.route
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
                modifier = Modifier.width(80.dp).verticalScroll(rememberScrollState())
            ) {
                NavigationRailItem(
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_home)) },
                    selected = currentRoute == AppRoute.HOME.route,
                    onClick = { navController.navigate(AppRoute.HOME.route) }
                )
                NavigationRailItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_calendaricon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_epg)) },
                    selected = currentRoute == AppRoute.EPG.route,
                    onClick = { navController.navigate(AppRoute.EPG.route) }
                )
                NavigationRailItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_channelsicon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_channels)) },
                    selected = currentRoute == AppRoute.CHANNELS.route,
                    onClick = { navController.navigate(AppRoute.CHANNELS.route) }
                )
                NavigationRailItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_cinemaicon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_movies)) },
                    selected = currentRoute == AppRoute.MOVIES.route,
                    onClick = { navController.navigate(AppRoute.MOVIES.route) }
                )
                NavigationRailItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_documentariesicon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_documentaries)) },
                    selected = currentRoute == AppRoute.DOCUMENTARIES.route,
                    onClick = { navController.navigate(AppRoute.DOCUMENTARIES.route) }
                )
                NavigationRailItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_serieslogoicon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_series)) },
                    selected = currentRoute == AppRoute.SERIES.route,
                    onClick = { navController.navigate(AppRoute.SERIES.route) }
                )
                NavigationRailItem(
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_sports)) },
                    selected = currentRoute == AppRoute.SPORTS.route,
                    onClick = { navController.navigate(AppRoute.SPORTS.route) }
                )
                NavigationRailItem(
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_adults)) },
                    selected = currentRoute == AppRoute.ADULTS.route,
                    onClick = { navController.navigate(AppRoute.ADULTS.route) }
                )
                NavigationRailItem(
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_kids)) },
                    selected = currentRoute == AppRoute.KIDS.route,
                    onClick = { navController.navigate(AppRoute.KIDS.route) }
                )
                NavigationRailItem(
                    icon = {
                        Icon(
                            Icons.Default.Search,
                            null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_search)) },
                    selected = currentRoute == AppRoute.SEARCH.route,
                    onClick = { navController.navigate(AppRoute.SEARCH.route) }
                )
                NavigationRailItem(
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_diagnostics)) },
                    selected = currentRoute == AppRoute.DIAGNOSTICS.route,
                    onClick = { navController.navigate(AppRoute.DIAGNOSTICS.route) }
                )
                NavigationRailItem(
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_change_user)) },
                    selected = currentRoute == "change_user",
                    onClick = { navController.navigate("change_user") }
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

    composable(AppRoute.DOCUMENTARIES.route) {
        HomeScreen(
            routeTag = AppRoute.DOCUMENTARIES,
            onContentClicked = { content ->
                navController.navigate(AppRoute.DETAIL.route) {
                    launchSingleTop = true
                }
                navController.currentBackStackEntry?.savedStateHandle?.set("content", content)
            }
        )
    }

    composable(AppRoute.SPORTS.route) {
        HomeScreen(
            routeTag = AppRoute.SPORTS,
            onContentClicked = { content ->
                navController.navigate(AppRoute.DETAIL.route) {
                    launchSingleTop = true
                }
                navController.currentBackStackEntry?.savedStateHandle?.set("content", content)
            }
        )
    }

    composable(AppRoute.KIDS.route) {
        HomeScreen(
            routeTag = AppRoute.KIDS,
            onContentClicked = { content ->
                navController.navigate(AppRoute.DETAIL.route) {
                    launchSingleTop = true
                }
                navController.currentBackStackEntry?.savedStateHandle?.set("content", content)
            }
        )
    }

    composable(AppRoute.ADULTS.route) {
        HomeScreen(
            routeTag = AppRoute.ADULTS,
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