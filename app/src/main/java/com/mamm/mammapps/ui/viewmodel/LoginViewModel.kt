package com.mamm.mammapps.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.domain.usecases.login.AutoLoginUseCase
import com.mamm.mammapps.domain.usecases.login.LoginUseCase
import com.mamm.mammapps.ui.model.uistate.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val autologinUseCase: AutoLoginUseCase
) : ViewModel() {

    companion object {
        const val TAG = "LoginViewModel"
    }

    private val _loginState = MutableStateFlow<UIState<Unit>>(UIState.Loading)
    val loginState: StateFlow<UIState<Unit>> = _loginState.asStateFlow()

    fun trytoAutoLogin() {
        viewModelScope.launch {
            _loginState.update { UIState.Loading }
            val result = autologinUseCase()

            result.fold(
                onSuccess = { data ->
                    Log.d(TAG, "Autologin successful")
                    _loginState.update { UIState.Success(data) }
                },
                onFailure = {
                    _loginState.update { UIState.Idle }
                }
            )
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.update { UIState.Loading }
            val result = loginUseCase(username, password)

            result.fold(
                onSuccess = { data ->
                    Log.d(TAG, "Login successful")
                    _loginState.update { UIState.Success(data) }
                },
                onFailure = { error ->
                    Log.e(TAG, "Login failed", error)
                    _loginState.update { UIState.Error(throwable = error) }
                }
            )
        }
    }
}