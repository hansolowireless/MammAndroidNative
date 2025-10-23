package com.mamm.mammapps.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
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
import com.mamm.mammapps.ui.component.icon.Puzzle
import com.mamm.mammapps.ui.component.navigation.CustomTVNavigationItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TVNavigationLayout(navController: NavHostController) {

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val sectionsWithMenu = remember {
        listOf(
            AppRoute.HOME.route,
            AppRoute.EPG.route,
            AppRoute.CHANNELS.route,
            AppRoute.MOVIES.route,
            AppRoute.DOCUMENTARIES.route,
            AppRoute.SPORTS.route,
            AppRoute.KIDS.route,
            AppRoute.SERIES.route,
            AppRoute.WARNER.route,
            AppRoute.ACONTRA.route,
            AppRoute.AMC.route,
            AppRoute.ADULTS.route,
            AppRoute.SEARCH.route,
            AppRoute.DIAGNOSTICS.route,
            AppRoute.LOGOUT.route
        ).distinct()
    }

    val showNavigationRail = currentRoute in sectionsWithMenu

    var isNavRailFocused by remember { mutableStateOf(false) }
    var isInitialFocusSet by remember { mutableStateOf(false) }

    val railWidth by animateDpAsState(
        targetValue = if (isNavRailFocused) 200.dp else 60.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "NavRailWidthAnimation"
    )

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val focusRequesters = remember { sectionsWithMenu.associateWith { FocusRequester() } }
    val bringIntoViewRequesters =
        remember { sectionsWithMenu.associateWith { BringIntoViewRequester() } }
    val itemPositions = remember { mutableMapOf<String, Float>() }

    // --- LÓGICA DE FOCO Y SCROLL ---
    LaunchedEffect(isNavRailFocused, currentRoute) {
        if (isNavRailFocused) {
            // El menú GANA foco
            if (currentRoute != null && !isInitialFocusSet) {
                // 1. Mueve el foco lógico al ítem correcto
                focusRequesters[currentRoute]?.requestFocus()
                // 2. Lanza una subtarea para ajustar el scroll
                coroutineScope.launch {
                    // Este delay es CRUCIAL. Evita la "guerra de scrolls".
                    delay(100)
                    bringIntoViewRequesters[currentRoute]?.bringIntoView()
                }
                isInitialFocusSet = true
            }
        } else {
            // El menú PIERDE foco. Reseteamos todo.
            isInitialFocusSet = false
            if (currentRoute != null) {
                itemPositions[currentRoute]?.let {
                    // Usamos corrutina para asegurar que el scroll se haga
                    coroutineScope.launch {
                        scrollState.scrollTo(it.toInt())
                    }
                }
            }
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        if (showNavigationRail) {
            ProvideLazyListPivotOffset(parentFraction = 0.01f) {
                // CAMBIO ARQUITECTÓNICO: El NavigationRail solo es un contenedor, ya no es scrollable.
                NavigationRail(
                    modifier = Modifier
                        .width(railWidth)
                        .fillMaxHeight()
                        // El onFocusEvent ahora está en el padre, que es quien tiene el foco lógico.
                        .onFocusEvent { focusState ->
                            isNavRailFocused = focusState.hasFocus
                        }
                        .focusable() // Es focusable para que onFocusEvent funcione.
                ) {
                    // CAMBIO ARQUITECTÓNICO: La Column interna es la que se encarga del scroll.
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .verticalScroll(scrollState)
                            .padding(top = 5.dp, bottom = 500.dp)
                    ) {
                        sectionsWithMenu.forEach { route ->
                            val itemModifier = Modifier
                                .focusRequester(focusRequesters.getValue(route))
                                .bringIntoViewRequester(bringIntoViewRequesters.getValue(route))
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
                                        parentIsFocused = isNavRailFocused,
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
                                        parentIsFocused = isNavRailFocused,
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
                                        parentIsFocused = isNavRailFocused,
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
                                        parentIsFocused = isNavRailFocused,
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
                                        parentIsFocused = isNavRailFocused,
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
                                        parentIsFocused = isNavRailFocused,
                                        selected = currentRoute == route,
                                        onClick = { navController.navigate(route) }
                                    )
                                }

                                AppRoute.KIDS.route -> {
                                    CustomTVNavigationItem(
                                        modifier = itemModifier,
                                        icon = {
                                            Icon(
                                                Puzzle,
                                                null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        label = stringResource(R.string.nav_kids),
                                        parentIsFocused = isNavRailFocused,
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
                                        parentIsFocused = isNavRailFocused,
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
                                        parentIsFocused = isNavRailFocused,
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
                                        parentIsFocused = isNavRailFocused,
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
                                        parentIsFocused = isNavRailFocused,
                                        selected = currentRoute == route,
                                        onClick = { navController.navigate(route) }
                                    )
                                }

                                AppRoute.ADULTS.route -> {
                                    CustomTVNavigationItem(
                                        modifier = itemModifier,
                                        icon = {
                                            Icon(
                                                Fire,
                                                null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        label = stringResource(R.string.nav_adults),
                                        parentIsFocused = isNavRailFocused,
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
                                        parentIsFocused = isNavRailFocused,
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
                                        parentIsFocused = isNavRailFocused,
                                        selected = currentRoute == route,
                                        onClick = { navController.navigate(route) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        NavHost(
            navController = navController,
            startDestination = AppRoute.LOGIN.route,
            modifier = Modifier.weight(1f)
        ) {
            navigationGraph(navController)
        }
    }
}

