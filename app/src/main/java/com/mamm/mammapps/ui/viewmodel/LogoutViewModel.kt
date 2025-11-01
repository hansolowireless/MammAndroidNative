package com.mamm.mammapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.usecases.login.GetOperatorLogoUseCase
import com.mamm.mammapps.domain.usecases.logout.LogoutUseCase
import com.mamm.mammapps.ui.model.uistate.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getOperatorLogoUseCase: GetOperatorLogoUseCase,
    private val logger: Logger
) : ViewModel() {

    companion object {
        private const val TAG = "LogoutViewModel"
    }

    private val _uiState = MutableStateFlow<UIState<*>>(UIState.Loading)
    val uiState: StateFlow<UIState<*>> = _uiState

    private val _operatorLogo = MutableStateFlow<String?>(null)
    val operatorLogo: StateFlow<String?> = _operatorLogo

    fun getOperatorLogo () {
        viewModelScope.launch {
            getOperatorLogoUseCase().onSuccess {
                logger.debug(TAG, "Operator logo URL: $it")
                _operatorLogo.value = it
            }
        }
    }

    fun logout() {
        // Lanzamos una corrutina en el scope del ViewModel para realizar
        // la operación de logout en segundo plano.
        viewModelScope.launch {
            logoutUseCase()
            _uiState.update { UIState.Success(Unit) }
        }
    }
}
