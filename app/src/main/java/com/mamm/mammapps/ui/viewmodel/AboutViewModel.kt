package com.mamm.mammapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.model.AboutInfo
import com.mamm.mammapps.domain.usecases.diagnostic.GetAboutInfoUseCase
import com.mamm.mammapps.domain.usecases.diagnostic.RunFullServerDiagnosticUseCase
import com.mamm.mammapps.domain.usecases.login.GetOperatorLogoUseCase
import com.mamm.mammapps.ui.model.uistate.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val getOperatorLogoUseCase: GetOperatorLogoUseCase,
    private val getAboutInfoUseCase: GetAboutInfoUseCase,
    private val runFullServerDiagnosticUseCase: RunFullServerDiagnosticUseCase,
    private val logger: Logger
) : ViewModel() {

    companion object {
        private const val TAG = "AboutViewModel"
    }

    private val _operatorLogo = MutableStateFlow<String?>(null)
    val operatorLogo: StateFlow<String?> = _operatorLogo

    private val _aboutInfo = MutableStateFlow<AboutInfo?>(null)
    val aboutInfo: StateFlow<AboutInfo?> = _aboutInfo

    private val _diagnosticUiState = MutableStateFlow<UIState<*>>(UIState.Idle)
    val diagnosticUiState: StateFlow<UIState<*>> = _diagnosticUiState

    fun getOperatorLogo() {
        viewModelScope.launch (Dispatchers.IO) {
            getOperatorLogoUseCase().onSuccess {
                logger.debug(TAG, "Operator logo URL: $it")
            }
        }
    }

    fun getAboutInfo() {
        viewModelScope.launch (Dispatchers.IO) {
            getAboutInfoUseCase().onSuccess {
                logger.debug(TAG, "AboutInfo: $it")
                _aboutInfo.value = it
            }.onFailure {
                logger.error(TAG, "Error getting about info, $it")
            }
        }
    }

    fun runDiagnostic() {
        viewModelScope.launch(Dispatchers.IO) {
            _diagnosticUiState.value = UIState.Loading

            logger.debug(TAG, "runDiagnostic Iniciando proceso de diagnóstico...")

            runFullServerDiagnosticUseCase().onSuccess { results ->
                logger.debug(TAG, "runDiagnostic Diagnóstico finalizado con ${results.count { it != null }} éxitos")
                _diagnosticUiState.value = UIState.Success(results)
            }.onFailure { error ->
                logger.error(TAG, "runDiagnostic Error en el diagnóstico: ${error.message}")
                _diagnosticUiState.value = UIState.Error(error.message ?: "Unknown error")
            }
        }
    }

}