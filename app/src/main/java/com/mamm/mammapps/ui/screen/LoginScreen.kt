package com.mamm.mammapps.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.hilt.navigation.compose.hiltViewModel
import com.mamm.mammapps.ui.common.UIState
import com.mamm.mammapps.ui.component.common.LoadingSpinner
import com.mamm.mammapps.ui.component.common.deviceAdaptivePadding
import com.mamm.mammapps.ui.component.login.LoginForm
import com.mamm.mammapps.ui.viewmodel.LoginViewModel
import kotlin.math.log

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {

    val loginState = viewModel.loginState

    LaunchedEffect (Unit) {
        viewModel.trytoAutoLogin()
    }

    LaunchedEffect(loginState) {
        when (loginState) {
            is UIState.Success<*> -> {
                onNavigateToHome()
            }
            is UIState.Error -> {

            }
            else -> {
                // Handle loading state
            }
        }
    }

    when (loginState) {

        is UIState.Success,
        is UIState.Loading -> {
            LoadingSpinner()
        }
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(deviceAdaptivePadding())
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
