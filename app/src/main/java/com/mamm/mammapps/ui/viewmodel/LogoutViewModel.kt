package com.mamm.mammapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState<*>>(UIState.Loading)
    val uiState: StateFlow<UIState<*>> = _uiState

    fun logout() {
        // Lanzamos una corrutina en el scope del ViewModel para realizar
        // la operación de logout en segundo plano.
        viewModelScope.launch {
            logoutUseCase()
            _uiState.update { UIState.Success(Unit) }
        }
    }
}
