package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.ui.component.common.LoadingSpinner
import com.mamm.mammapps.ui.component.common.deviceAdaptivePadding
import com.mamm.mammapps.ui.component.login.LoginForm
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.ui.theme.MammAppsTheme
import com.mamm.mammapps.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.trytoAutoLogin()
    }

    LaunchedEffect(loginState) {
        if (loginState is UIState.Success<*>) {
            onNavigateToHome()
        }
    }

    // El Box principal ahora sirve como contenedor de fondo.
    Box(modifier = Modifier.fillMaxSize()) {
        when (loginState) {
            is UIState.Success<*>,
            is UIState.Loading -> {
                LoadingSpinner(modifier = Modifier.align(Alignment.Center))
            }
            else -> {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxWidth(0.65f)
                        .fillMaxHeight()
                        .padding(deviceAdaptivePadding()),
                    contentAlignment = Alignment.Center
                ) {
                    LoginForm(
                        onLogin = { email, password ->
                            viewModel.login(email, password)
                        }
                    )
                }
            }
        }
    }
}

