package com.mamm.mammapps.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.domain.usecases.AutoLoginUseCase
import com.mamm.mammapps.domain.usecases.LoginUseCase
import com.mamm.mammapps.ui.common.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
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

    var loginState by mutableStateOf<UIState<Unit>>(UIState.Loading)
        private set

    fun trytoAutoLogin() {
        viewModelScope.launch {
            loginState =  UIState.Loading
            val result = autologinUseCase()

            result.fold(
                onSuccess = { data ->
                    Log.d(TAG, "Autologin successful")
                    loginState = UIState.Success(data)
                },
                onFailure = {
                    loginState = UIState.Idle
                }
            )
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            loginState = UIState.Loading
            val result = loginUseCase(username, password)

            loginState =  UIState.Loading

            result.fold(
                onSuccess = { data ->
                    Log.d(TAG, "Login successful")
                    loginState = UIState.Success(data)
                },
                onFailure = { error ->
                    loginState = UIState.Error(error.message ?: "Error desconocido")
                }
            )
        }
    }
}