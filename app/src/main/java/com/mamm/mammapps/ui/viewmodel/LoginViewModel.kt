package com.mamm.mammapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.usecases.login.AutoLoginUseCase
import com.mamm.mammapps.domain.usecases.login.GenerateLoginCodeUseCase
import com.mamm.mammapps.domain.usecases.login.LoginUseCase
import com.mamm.mammapps.domain.usecases.login.PollLoginCodeStatusUseCase
import com.mamm.mammapps.domain.model.loginwithcode.LoginCodeGenerate
import com.mamm.mammapps.domain.usecases.logout.LogoutUseCase
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.util.secondsToMiilliseconds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val autologinUseCase: AutoLoginUseCase,
    private val generateLoginCodeUseCase: GenerateLoginCodeUseCase,
    private val pollLoginCodeStatusUseCase: PollLoginCodeStatusUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val logger: Logger
) : ViewModel() {

    companion object {
        const val TAG = "LoginViewModel"
    }

    private val _loginState = MutableStateFlow<UIState<Unit>>(UIState.Loading)
    val loginState: StateFlow<UIState<Unit>> = _loginState.asStateFlow()

    private val _tvCode = MutableStateFlow<LoginCodeGenerate?>(null)
    val tvCode: StateFlow<LoginCodeGenerate?> = _tvCode.asStateFlow()

    private var pollingJob: Job? = null

    fun setIdleState() {
        _loginState.update { UIState.Idle }
    }

    fun generateTvCode() {
        viewModelScope.launch {
            val result = generateLoginCodeUseCase()
            result.onSuccess { data ->
                _tvCode.value = data
                startPollingTvCode(data.code, data.pollingInterval)
            }.onFailure { error ->
                logger.error(TAG, "Error generating TV code: $error")
            }
        }
    }

    private fun startPollingTvCode(code: String, intervalSeconds: Int) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(intervalSeconds.secondsToMiilliseconds())
                pollLoginCodeStatusUseCase(code)
                    .onSuccess { status ->
                        if (status.data != null) {
                            logger.debug(TAG, "TV code login successful")
                            pollingJob?.cancel()
                            _loginState.update { UIState.Success(Unit) }
                        } else if (status.result == "EXPIRED") {
                            logger.debug(TAG, "TV code expired from server")
                            pollingJob?.cancel()
                        }
                    }.onFailure { error ->
                        logger.error(TAG, "Error polling TV code status: $error")
                    }
            }
        }
    }

    fun cancelTvCodePolling() {
        logger.debug(TAG, "Cancelling TV code polling")
        pollingJob?.cancel()
    }

    fun trytoAutoLogin() {
        logger.debug(TAG, "trytoAutoLogin")
        viewModelScope.launch {
            _loginState.update { UIState.Loading }
            val result = autologinUseCase()

            result.fold(
                onSuccess = { data ->
                    logger.debug(TAG, "Autologin successful")
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
                    logger.debug(TAG, "Login successful")

                    _loginState.update { UIState.Success(data) }
                },
                onFailure = { error ->
                    logger.error(TAG, "Login failed $error")
                    _loginState.update { UIState.Error(throwable = error) }
                }
            )
        }
    }
}