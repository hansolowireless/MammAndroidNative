package com.mamm.mammapps.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.common.LoadingSpinner
import com.mamm.mammapps.ui.component.common.deviceAdaptivePadding
import com.mamm.mammapps.ui.component.login.LoginForm
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

    LaunchedEffect (loginState) {
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
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(0.4f),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_masmedia),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .fillMaxHeight(0.5f),
                            contentScale = ContentScale.Fit
                        )
                    }

                    VerticalDivider(
                        modifier = Modifier
                            .fillMaxHeight(0.6f)
                            .width(1.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Box(
                        modifier = Modifier
                            .weight(0.6f)
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
}
