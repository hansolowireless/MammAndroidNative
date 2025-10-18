package com.mamm.mammapps.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mamm.mammapps.R
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.component.icon.BulletedList
import com.mamm.mammapps.ui.component.icon.Fire
import com.mamm.mammapps.ui.component.icon.Football
import com.mamm.mammapps.ui.component.icon.WifiSignal
import com.mamm.mammapps.ui.component.navigation.CustomTVNavigationItem
import kotlinx.coroutines.delay

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
        AppRoute.WARNER.route,
        AppRoute.ACONTRA.route,
        AppRoute.AMC.route,
        AppRoute.SEARCH.route,
        AppRoute.DIAGNOSTICS.route,
        AppRoute.ADULTS.route,
        AppRoute.LOGOUT.route
    ).distinct()

    val showNavigationRail = currentRoute in sectionsWithMenu
    var hasItemFocused by remember { mutableStateOf(false) }
    val railWidth by animateDpAsState(
        targetValue = if (hasItemFocused) 200.dp else 60.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )
    val scrollState = rememberScrollState()

    val focusRequesters = remember {
        sectionsWithMenu.associateWith { FocusRequester() }
    }
    var isInitialFocusSet by remember { mutableStateOf(false) }
    val navRailRequester = remember { FocusRequester() }
    val itemPositions = remember { mutableStateMapOf<String, Float>() }

    // Se ejecuta cuando el menú gana el foco para establecer el foco inicial.
    LaunchedEffect(hasItemFocused, currentRoute) {
        if (hasItemFocused && currentRoute != null && !isInitialFocusSet) {
            delay(50)
            focusRequesters[currentRoute]?.requestFocus()
            isInitialFocusSet = true
        }
    }

    // Se ejecuta cuando el estado de foco del menú cambia.
    LaunchedEffect(hasItemFocused, currentRoute) {
        // Cuando el menú pierde el foco (se sale de él)...
        if (!hasItemFocused) {
            isInitialFocusSet = false
            // ...y tenemos una ruta actual y su posición...
            if (currentRoute != null) {
                itemPositions[currentRoute]?.let { position ->
                    // ...reseteamos el scroll a la posición de ese item.
                    scrollState.scrollTo(position.toInt())
                }
            }
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        if (showNavigationRail) {
            ProvideLazyListPivotOffset(parentFraction = 0.01f) {
                NavigationRail(
                    modifier = Modifier
                        .width(railWidth)
                        .fillMaxHeight()
                        .verticalScroll(scrollState, enabled = isInitialFocusSet)
                        .padding(top = 5.dp, bottom = 500.dp)
                        .onFocusChanged { hasItemFocused = it.hasFocus }
                        .focusRequester(navRailRequester)
                        .focusProperties {
                            // Al entrar al NavRail desde fuera, cancelamos el foco automático
                            // para que nuestro LaunchedEffect decida a dónde va.
                            onEnter = { FocusRequester.Cancel }

                            if (!isInitialFocusSet) {
                                down = navRailRequester
                                up = navRailRequester
                                left = navRailRequester
                                right = navRailRequester
                            }
                        }
                ) {
                    sectionsWithMenu.forEach { route ->

                        val itemModifier = Modifier
                            .focusRequester(focusRequesters.getValue(route))
                            .onGloballyPositioned { coordinates ->
                                itemPositions[route] = coordinates.positionInParent().y
                            }

                        when (route) {
                            AppRoute.HOME.route -> {
                                CustomTVNavigationItem(
                                    modifier = itemModifier,
                                    icon = {
                                        Icon(
                                            Icons.Default.Home,
                                            null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = stringResource(R.string.nav_home),
                                    parentIsFocused = hasItemFocused,
                                    selected = currentRoute == route,
                                    onClick = { navController.navigate(route) }
                                )
                            }

                            AppRoute.EPG.route -> {
                                CustomTVNavigationItem(
                                    modifier = itemModifier,
                                    icon = {
                                        Icon(
                                            BulletedList,
                                            null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = stringResource(R.string.nav_epg),
                                    parentIsFocused = hasItemFocused,
                                    selected = currentRoute == route,
                                    onClick = { navController.navigate(route) }
                                )
                            }

                            AppRoute.CHANNELS.route -> {
                                CustomTVNavigationItem(
                                    modifier = itemModifier,
                                    icon = {
                                        Icon(
                                            painterResource(id = R.drawable.menu_channelsicon),
                                            null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = stringResource(R.string.nav_channels),
                                    parentIsFocused = hasItemFocused,
                                    selected = currentRoute == route,
                                    onClick = { navController.navigate(route) }
                                )
                            }

                            AppRoute.MOVIES.route -> {
                                CustomTVNavigationItem(
                                    modifier = itemModifier,
                                    icon = {
                                        Icon(
                                            painterResource(id = R.drawable.menu_cinemaicon),
                                            null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = stringResource(R.string.nav_movies),
                                    parentIsFocused = hasItemFocused,
                                    selected = currentRoute == route,
                                    onClick = { navController.navigate(route) }
                                )
                            }

                            AppRoute.DOCUMENTARIES.route -> {
                                CustomTVNavigationItem(
                                    modifier = itemModifier,
                                    icon = {
                                        Icon(
                                            painterResource(id = R.drawable.menu_documentariesicon),
                                            null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = stringResource(R.string.nav_documentaries),
                                    parentIsFocused = hasItemFocused,
                                    selected = currentRoute == route,
                                    onClick = { navController.navigate(route) }
                                )
                            }

                            AppRoute.SERIES.route -> {
                                CustomTVNavigationItem(
                                    modifier = itemModifier,
                                    icon = {
                                        Icon(
                                            painterResource(id = R.drawable.menu_serieslogoicon),
                                            null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = stringResource(R.string.nav_series),
                                    parentIsFocused = hasItemFocused,
                                    selected = currentRoute == route,
                                    onClick = { navController.navigate(route) }
                                )
                            }

                            AppRoute.WARNER.route -> {
                                CustomTVNavigationItem(
                                    modifier = itemModifier,
                                    icon = {
                                        Icon(
                                            painterResource(id = R.drawable.menu_wblogoicon),
                                            null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = stringResource(R.string.nav_warner),
                                    parentIsFocused = hasItemFocused,
                                    selected = currentRoute == route,
                                    onClick = { navController.navigate(route) }
                                )
                            }

                            AppRoute.ACONTRA.route -> {
                                CustomTVNavigationItem(
                                    modifier = itemModifier,
                                    icon = {
                                        Icon(
                                            painterResource(id = R.drawable.menu_acontralogoicon),
                                            null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = stringResource(R.string.nav_acontra),
                                    parentIsFocused = hasItemFocused,
                                    selected = currentRoute == route,
                                    onClick = { navController.navigate(route) }
                                )
                            }

                            AppRoute.AMC.route -> {
                                CustomTVNavigationItem(
                                    modifier = itemModifier,
                                    icon = {
                                        Icon(
                                            painterResource(id = R.drawable.menu_amclogoicon),
                                            null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = stringResource(R.string.nav_amc),
                                    parentIsFocused = hasItemFocused,
                                    selected = currentRoute == route,
                                    onClick = { navController.navigate(route) }
                                )
                            }

                            AppRoute.SPORTS.route -> {
                                CustomTVNavigationItem(
                                    modifier = itemModifier,
                                    icon = {
                                        Icon(
                                            Football,
                                            null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = stringResource(R.string.nav_sports),
                                    parentIsFocused = hasItemFocused,
                                    selected = currentRoute == route,
                                    onClick = { navController.navigate(route) }
                                )
                            }

                            AppRoute.ADULTS.route -> {
                                CustomTVNavigationItem(
                                    modifier = itemModifier,
                                    icon = { Icon(Fire, null, modifier = Modifier.size(24.dp)) },
                                    label = stringResource(R.string.nav_adults),
                                    parentIsFocused = hasItemFocused,
                                    selected = currentRoute == route,
                                    onClick = { navController.navigate(route) }
                                )
                            }

                            AppRoute.KIDS.route -> {
                                CustomTVNavigationItem(
                                    modifier = itemModifier,
                                    icon = {
                                        Icon(
                                            Icons.Default.Person,
                                            null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = stringResource(R.string.nav_kids),
                                    parentIsFocused = hasItemFocused,
                                    selected = currentRoute == route,
                                    onClick = { navController.navigate(route) }
                                )
                            }

                            AppRoute.SEARCH.route -> {
                                CustomTVNavigationItem(
                                    modifier = itemModifier,
                                    icon = {
                                        Icon(
                                            Icons.Default.Search,
                                            null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = stringResource(R.string.nav_search),
                                    parentIsFocused = hasItemFocused,
                                    selected = currentRoute == route,
                                    onClick = { navController.navigate(route) }
                                )
                            }

                            AppRoute.DIAGNOSTICS.route -> {
                                CustomTVNavigationItem(
                                    modifier = itemModifier,
                                    icon = {
                                        Icon(
                                            WifiSignal,
                                            null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = stringResource(R.string.nav_diagnostics),
                                    parentIsFocused = hasItemFocused,
                                    selected = currentRoute == route,
                                    onClick = { navController.navigate(route) }
                                )
                            }

                            AppRoute.LOGOUT.route -> {
                                CustomTVNavigationItem(
                                    modifier = itemModifier,
                                    icon = {
                                        Icon(
                                            Icons.Default.Person,
                                            null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = stringResource(R.string.nav_change_user),
                                    parentIsFocused = hasItemFocused,
                                    selected = currentRoute == route,
                                    onClick = { navController.navigate(route) }
                                )
                            }
                        }
                    }
                }
            }
        }
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.weight(1f)
        ) {
            navigationGraph(navController)
        }
    }
}
