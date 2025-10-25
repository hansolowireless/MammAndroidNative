package com.mamm.mammapps.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.component.common.LoadingSpinner
import com.mamm.mammapps.ui.component.login.LoginMobile
import com.mamm.mammapps.ui.component.login.LoginTV
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.trytoAutoLogin()
    }

    LaunchedEffect(loginState) {
        if (loginState is UIState.Success<*>) {
            onNavigateToHome()
        }
    }

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is UIState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (loginState) {
            is UIState.Success<*>,
            is UIState.Loading -> {
                LoadingSpinner(modifier = Modifier.align(Alignment.Center))
            }

            else -> {
                if (LocalIsTV.current) {
                    LoginTV(onLogin = { email, password -> viewModel.login(email, password) })
                } else {
                    LoginMobile(onLogin = { email, password -> viewModel.login(email, password) })
                }
            }
        }
    }
}
