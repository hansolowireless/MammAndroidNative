package com.mamm.mammapps.navigation.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mamm.mammapps.navigation.MenuItems
import com.mamm.mammapps.navigation.extension.navigateToTopLevel
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.component.navigation.CustomTVNavigationItem

/**
 * Composable que representa la lista de navegación lateral para TV.
 *
 * @param navController Controlador de navegación.
 * @param menuItems Lista de rutas que aparecerán en el menú.
 * @param currentRoute Ruta actual para marcar el item seleccionado.
 * @param isNavRailFocused Estado que indica si el menú lateral tiene el foco.
 * @param lazyListState Estado de la lista para controlar scroll.
 * @param focusRequesters Mapa de Requesters para gestionar el foco de cada item.
 * @param bringIntoViewRequesters Mapa para asegurar que el item enfocado sea visible.
 * @param itemPositions Mapa para trackear la posición Y de los elementos (útil para efectos visuales).
 */
@Composable
fun TVMenuList(
    navController: NavHostController,
    menuItems: List<AppRoute>,
    currentRoute: String?,
    isNavRailFocused: Boolean,
    lazyListState: LazyListState,
    focusRequesters: Map<String, FocusRequester>,
    bringIntoViewRequesters: Map<String, BringIntoViewRequester>,
    itemPositions: MutableMap<String, Float>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 5.dp),
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 10.dp) // Mejor que un Spacer gigante
    ) {
        items(menuItems) { item ->
            val routeKey = item.route
            val isLastItem = menuItems.last().route == routeKey

            val itemModifier = Modifier
                .focusRequester(focusRequesters[routeKey] ?: FocusRequester.Default)
                .bringIntoViewRequester(bringIntoViewRequesters[routeKey] ?: BringIntoViewRequester())
                .onGloballyPositioned { coordinates ->
                    itemPositions[routeKey] = coordinates.positionInParent().y
                }
                .onKeyEvent { keyEvent ->
                    // Evita que el foco "salga" hacia abajo si estamos en el último elemento
                    if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionDown) {
                        if (isLastItem) return@onKeyEvent true
                    }
                    false
                }

            CustomTVNavigationItem(
                modifier = itemModifier,
                icon = { MenuItems.GetIconForRoute(route = item) },
                label = stringResource(id = item.getResId()),
                parentIsFocused = isNavRailFocused,
                selected = currentRoute == routeKey,
                onClick = {
                    handleNavigation(navController, item)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(1000.dp))
        }
    }
}

/**
 * Lógica de navegación extraída para mayor claridad
 */
private fun handleNavigation(navController: NavHostController, item: AppRoute) {
    if (item.route == AppRoute.HOME.route) {
        navController.navigate(item.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    } else {
        navController.navigateToTopLevel(item.route)
    }
}