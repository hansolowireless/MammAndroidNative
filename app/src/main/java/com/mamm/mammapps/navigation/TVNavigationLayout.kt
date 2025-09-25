package com.mamm.mammapps.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.component.navigation.CustomTVNavigationItem
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.theme.Dimensions

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
    var hasItemFocused by remember { mutableStateOf(false) }
    val railWidth by animateDpAsState(
        targetValue = if (hasItemFocused) 200.dp else 80.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    Row {
        if (showNavigationRail) {
            ProvideLazyListPivotOffset(parentFraction = 0.0f) {
                NavigationRail(
                    modifier = Modifier
                        .width(railWidth)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(top = Dimensions.paddingSmall, bottom = 300.dp)
                        .onFocusChanged { hasItemFocused = it.hasFocus }
                ) {
                    CustomTVNavigationItem(
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = stringResource(R.string.nav_home),
                        parentIsFocused = hasItemFocused,
                        selected = currentRoute == AppRoute.HOME.route,
                        onClick = { navController.navigate(AppRoute.HOME.route) }
                    )

                    CustomTVNavigationItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.menu_calendaricon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = stringResource(R.string.nav_epg),
                        parentIsFocused = hasItemFocused,
                        selected = currentRoute == AppRoute.EPG.route,
                        onClick = { navController.navigate(AppRoute.EPG.route) }
                    )

                    CustomTVNavigationItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.menu_channelsicon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = stringResource(R.string.nav_channels),
                        parentIsFocused = hasItemFocused,
                        selected = currentRoute == AppRoute.CHANNELS.route,
                        onClick = { navController.navigate(AppRoute.CHANNELS.route) }
                    )

                    CustomTVNavigationItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.menu_cinemaicon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = stringResource(R.string.nav_movies),
                        parentIsFocused = hasItemFocused,
                        selected = currentRoute == AppRoute.MOVIES.route,
                        onClick = { navController.navigate(AppRoute.MOVIES.route) }
                    )

                    CustomTVNavigationItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.menu_documentariesicon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = stringResource(R.string.nav_documentaries),
                        parentIsFocused = hasItemFocused,
                        selected = currentRoute == AppRoute.DOCUMENTARIES.route,
                        onClick = { navController.navigate(AppRoute.DOCUMENTARIES.route) }
                    )

                    CustomTVNavigationItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.menu_serieslogoicon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = stringResource(R.string.nav_series),
                        parentIsFocused = hasItemFocused,
                        selected = currentRoute == AppRoute.SERIES.route,
                        onClick = { navController.navigate(AppRoute.SERIES.route) }
                    )

                    CustomTVNavigationItem(
                        icon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = stringResource(R.string.nav_sports),
                        parentIsFocused = hasItemFocused,
                        selected = currentRoute == AppRoute.SPORTS.route,
                        onClick = { navController.navigate(AppRoute.SPORTS.route) }
                    )

                    CustomTVNavigationItem(
                        icon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = stringResource(R.string.nav_adults),
                        parentIsFocused = hasItemFocused,
                        selected = currentRoute == AppRoute.ADULTS.route,
                        onClick = { navController.navigate(AppRoute.ADULTS.route) }
                    )

                    CustomTVNavigationItem(
                        icon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = stringResource(R.string.nav_kids),
                        parentIsFocused = hasItemFocused,
                        selected = currentRoute == AppRoute.KIDS.route,
                        onClick = { navController.navigate(AppRoute.KIDS.route) }
                    )

                    CustomTVNavigationItem(
                        icon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = stringResource(R.string.nav_search),
                        parentIsFocused = hasItemFocused,
                        selected = currentRoute == AppRoute.SEARCH.route,
                        onClick = { navController.navigate(AppRoute.SEARCH.route) }
                    )

                    CustomTVNavigationItem(
                        icon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = stringResource(R.string.nav_diagnostics),
                        parentIsFocused = hasItemFocused,
                        selected = currentRoute == AppRoute.DIAGNOSTICS.route,
                        onClick = { navController.navigate(AppRoute.DIAGNOSTICS.route) }
                    )

                    CustomTVNavigationItem(
                        icon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = stringResource(R.string.nav_change_user),
                        parentIsFocused = hasItemFocused,
                        selected = currentRoute == AppRoute.LOGOUT.route,
                        onClick = { navController.navigate(AppRoute.LOGOUT.route) }
                    )

                    Spacer(modifier = Modifier.height(1000.dp))
                }
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