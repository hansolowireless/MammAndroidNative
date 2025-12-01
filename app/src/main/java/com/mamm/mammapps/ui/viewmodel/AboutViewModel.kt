package com.mamm.mammapps.ui.viewmodel

import androidx.coordinatorlayout.widget.CoordinatorLayout.DispatchChangeEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.AboutInfo
import com.mamm.mammapps.domain.usecases.GetAboutInfoUseCase
import com.mamm.mammapps.domain.usecases.login.GetOperatorLogoUseCase
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
    private val logger: Logger
) : ViewModel() {

    companion object {
        private const val TAG = "AboutViewModel"
    }

    private val _operatorLogo = MutableStateFlow<String?>(null)
    val operatorLogo: StateFlow<String?> = _operatorLogo

    private val _aboutInfo = MutableStateFlow<AboutInfo?>(null)
    val aboutInfo: StateFlow<AboutInfo?> = _aboutInfo

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

}