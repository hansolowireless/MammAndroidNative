package com.mamm.mammapps.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mamm.mammapps.R
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.component.chromecast.CastButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileNavigationLayout(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val menuItems = MenuItems.list
    val showNavigationDrawer = currentRoute in menuItems.map { it.route }

    ModalNavigationDrawer(
        drawerState = drawerState,
        // Habilitar/deshabilitar gestos para abrir el cajón
        gesturesEnabled = showNavigationDrawer,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.padding(12.dp))
                LazyColumn {
                    items(menuItems) { item ->
                        NavigationDrawerItem(
                            icon = { MenuItems.GetIconForRoute(route = item) },
                            label = { Text(stringResource(id = item.getResId())) },
                            selected = currentRoute == item.route,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate(item.route)
                            },
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (showNavigationDrawer) {
                    TopAppBar(
                        title = {
                            Text(text = currentRoute?.let { stringResource(AppRoute.fromRoute(currentRoute)?.getResId() ?: R.string.empty) }
                                ?: "")
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            }) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = stringResource(R.string.accessibility_toggle_drawer)
                                )
                            }
                        },
                        actions = {
                            CastButton()
                        }
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = AppRoute.LOGIN.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                navigationGraph(navController)
            }
        }
    }
}

