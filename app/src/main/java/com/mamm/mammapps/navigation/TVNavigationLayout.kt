package com.mamm.mammapps.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
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
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mamm.mammapps.navigation.component.TVMenuList
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.navigation.viewModel.NavigationViewModel
import com.mamm.mammapps.ui.component.common.OperatorLogoImage
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.theme.Dimensions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TVNavigationLayout(
    navController: NavHostController,
    viewModel: NavigationViewModel = hiltViewModel()
) {

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val menuItems by viewModel.menuItems.collectAsStateWithLifecycle()
    val operatorLogo by viewModel.operatorLogo.collectAsStateWithLifecycle()

    val routeList = menuItems.map { it.route }

    var isNavRailFocused by remember { mutableStateOf(false) }
    var isInitialFocusSet by remember { mutableStateOf(false) }
    var canFocusAfterRecompose by remember { mutableStateOf(false) }

    val railWidth by animateDpAsState(
        targetValue = if (isNavRailFocused && canFocusAfterRecompose) Dimensions.tvNavRailWidthExpanded else Dimensions.tvNavRailWidth,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "NavRailWidthAnimation"
    )

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val focusRequesters = remember { routeList.associateWith { FocusRequester() } }
    val bringIntoViewRequesters = remember { routeList.associateWith { BringIntoViewRequester() } }
    val itemPositions = remember { mutableMapOf<String, Float>() }

    LaunchedEffect(currentRoute) {
        if (currentRoute == AppRoute.HOME.route) {
            viewModel.setMenuItems()
            viewModel.getOperatorLogo()
        }
    }

    LaunchedEffect(currentRoute) {
        delay(1500)
        canFocusAfterRecompose = true
    }

    // --- LÓGICA DE FOCO Y SCROLL ---
    LaunchedEffect(isNavRailFocused, currentRoute) {
        if (isNavRailFocused) {
            if (currentRoute != null && !isInitialFocusSet) {
                focusRequesters[currentRoute]?.requestFocus()
                coroutineScope.launch {
                    delay(100)
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
        if (AppRoute.fromRoute(currentRoute)?.let { MenuItems.showSideMenu(it) } == true) {
            ProvideLazyListPivotOffset(parentFraction = 0.01f) {
                NavigationRail(
                    modifier = Modifier
                        .width(railWidth)
                        .onFocusEvent { focusState ->
                            isNavRailFocused = focusState.hasFocus
                        }
                        .focusable(canFocusAfterRecompose)
                ) {
                    // Usamos un Column para organizar el Menú (arriba) y el Logo (abajo)
                    Column(modifier = Modifier.fillMaxHeight()) {

                        // El menú ocupa todo el espacio disponible, empujando lo demás abajo
                        TVMenuList(
                            navController = navController,
                            menuItems = menuItems,
                            currentRoute = currentRoute,
                            isNavRailFocused = isNavRailFocused,
                            lazyListState = lazyListState,
                            focusRequesters = focusRequesters,
                            bringIntoViewRequesters = bringIntoViewRequesters,
                            itemPositions = itemPositions,
                            modifier = Modifier
                                .weight(1f) // Esto hace que el scroll sea independiente
                                .padding(vertical = 5.dp)
                        )

                        // Imagen fija en la parte inferior
                        if (MenuItems.showLogoOnMenu() && isNavRailFocused) {
                            OperatorLogoImage(
                                logoUrl = operatorLogo,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .padding(16.dp),
                            )
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

