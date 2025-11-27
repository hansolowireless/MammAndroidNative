package com.mamm.mammapps.navigation

import android.view.Menu
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.navigation.viewModel.NavigationViewModel
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.component.navigation.CustomTVNavigationItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TVNavigationLayout(
    navController: NavHostController,
    viewModel: NavigationViewModel = hiltViewModel()
) {

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val menuItems by viewModel.menuItems.collectAsStateWithLifecycle()

    val routeList = menuItems.map { it.route }
    val showNavigationRail = currentRoute in routeList
    var isNavRailFocused by remember { mutableStateOf(false) }
    var isInitialFocusSet by remember { mutableStateOf(false) }
    var canFocusAfterRecompose by remember { mutableStateOf(false) }

    val railWidth by animateDpAsState(
        targetValue = if (isNavRailFocused && canFocusAfterRecompose) 200.dp else 60.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "NavRailWidthAnimation"
    )

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val focusRequesters = remember { routeList.associateWith { FocusRequester() } }
    val bringIntoViewRequesters = remember { routeList.associateWith { BringIntoViewRequester() } }
    val itemPositions = remember { mutableMapOf<String, Float>() }

    LaunchedEffect (currentRoute) {
        if (currentRoute == AppRoute.HOME.route)
            viewModel.setMenuItems()
    }

    LaunchedEffect (currentRoute) {
        delay(3000)
        canFocusAfterRecompose = true
    }

    // --- LÓGICA DE FOCO Y SCROLL ---
    LaunchedEffect(isNavRailFocused, currentRoute) {
        if (isNavRailFocused) {
            // ... (Tu lógica para cuando GANA el foco está mayormente bien)
            // Podrías incluso mejorarla usando el índice aquí también
            if (currentRoute != null && !isInitialFocusSet) {
                focusRequesters[currentRoute]?.requestFocus()
                coroutineScope.launch {
                    delay(100) // El delay sigue siendo una buena práctica
                    bringIntoViewRequesters[currentRoute]?.bringIntoView()
                }
                isInitialFocusSet = true
            }

        } else {
            // El menú PIERDE foco. Reseteamos.
            isInitialFocusSet = false
            if (currentRoute != null) {
                // Encuentra el ÍNDICE del item actual en tu lista de menú
                val itemIndex = menuItems.indexOfFirst { it.route == currentRoute }

                // Si se encuentra el índice, haz scroll a ese item
                if (itemIndex != -1) {
                    coroutineScope.launch {
                        // 3. Usa el método del LazyListState para hacer scroll
                        lazyListState.animateScrollToItem(index = itemIndex)
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
                        .focusable(canFocusAfterRecompose) // Es focusable para que onFocusEvent funcione.
                ) {


                    LazyColumn(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(top = 5.dp, bottom = 5.dp),
                        state = lazyListState
                    ) {
                        items(menuItems) { item ->

                            val itemModifier = Modifier
                                .focusRequester(focusRequesters.getValue(item.route))
                                .bringIntoViewRequester(bringIntoViewRequesters.getValue(item.route))
                                .onGloballyPositioned { coordinates ->
                                    itemPositions[item.route] = coordinates.positionInParent().y
                                }

                            CustomTVNavigationItem(
                                modifier = itemModifier,
                                icon = { MenuItems.GetIconForRoute(route = item) },
                                label = stringResource(id = item.getResId()),
                                parentIsFocused = isNavRailFocused,
                                selected = currentRoute == item.route,
                                onClick = { navController.navigate(item.route) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(1000.dp))
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

