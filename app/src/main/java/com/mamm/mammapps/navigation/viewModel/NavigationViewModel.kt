package com.mamm.mammapps.navigation.viewModel

import androidx.lifecycle.ViewModel
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.LoginRepository
import com.mamm.mammapps.domain.usecases.login.GetOperatorLogoUseCase
import com.mamm.mammapps.domain.usecases.navigation.GetMenuItemsUseCase
import com.mamm.mammapps.navigation.MenuItems
import com.mamm.mammapps.navigation.model.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val getOperatorLogoUseCase: GetOperatorLogoUseCase,
    private val getMenuItemsUseCase: GetMenuItemsUseCase,
    private val logger: Logger
) : ViewModel() {

    companion object {
        private const val TAG = "NavigationViewModel"
    }

    private val _menuItems = MutableStateFlow<List<AppRoute>>(MenuItems.list)
    val menuItems: StateFlow<List<AppRoute>> = _menuItems.asStateFlow()

    private val _operatorLogo = MutableStateFlow<String?>(null)
    val operatorLogo: StateFlow<String?> = _operatorLogo.asStateFlow()

    fun setMenuItems() {
        getMenuItemsUseCase().onSuccess { items ->
            logger.debug(TAG, "Actualizando UI con ${items.size} elementos de menú")
            _menuItems.value = items
        }.onFailure {
            logger.error(TAG, "Fallo al setear menús en el ViewModel")
        }
    }

    fun getOperatorLogo() {
        getOperatorLogoUseCase().onSuccess {
            _operatorLogo.value = it
        }.onFailure {
            logger.error(TAG, "getOperatorLogo Error getting operator logo: $it")
            _operatorLogo.value = null
        }
    }


}