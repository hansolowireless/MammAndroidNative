package com.mamm.mammapps.navigation

import android.util.Log
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
import androidx.navigation.compose.rememberNavController
import com.mamm.mammapps.data.model.EPGEvent
import com.mamm.mammapps.data.model.Event
import com.mamm.mammapps.data.model.VoD
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.mapper.toContentDetailUI
import com.mamm.mammapps.ui.mapper.toContentEntityUI
import com.mamm.mammapps.ui.model.RouteTag
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
    }
}

@Composable
fun TVNavigationLayout(navController: NavHostController) {
    Row {
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
                startDestination = "login",
                modifier = Modifier.fillMaxSize()
            ) {
                navigationGraph(navController)
            }
        }
    }
}

fun NavGraphBuilder.navigationGraph(navController: NavHostController) {
    composable("login") {
        LoginScreen(
            onNavigateToHome = {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        )
    }

    composable("home") {
        HomeScreen(
            onContentClicked = { content ->
                navController.navigate("detail") {
                    launchSingleTop = true
                }
                navController.currentBackStackEntry?.savedStateHandle?.set("content", content)
            }
        )
    }

    composable("movies") {
        HomeScreen (
            routeTag = RouteTag.MOVIES,
            onContentClicked = { content ->
                navController.navigate("detail") {
                    launchSingleTop = true
                }
                navController.currentBackStackEntry?.savedStateHandle?.set("content", content)
            }
        )
    }

    composable("detail") { backStackEntry ->
        val contentItem = remember(backStackEntry) {
            backStackEntry.savedStateHandle.get<Any>("content")
        }

        contentItem?.let {
            DetailScreen(
                content = when (it) {
                    is VoD -> it.toContentDetailUI()
                    is Event -> it.toContentDetailUI()
                    is EPGEvent -> it.toContentDetailUI()
                    else -> return@composable
                },
                onPlayClick = { TODO() }
            )
        } ?: Text("No content available")
    }

    composable("epg") {
        EPGScreen()
    }



}