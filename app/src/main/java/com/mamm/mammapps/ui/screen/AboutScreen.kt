package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.R
import com.mamm.mammapps.domain.model.about.DownloadSpeedResult
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.component.common.OperatorLogoImage
import com.mamm.mammapps.ui.component.common.PrimaryButton
import com.mamm.mammapps.ui.component.dialog.PinDialog
import com.mamm.mammapps.ui.component.diagnostic.DiagnosticResultsList
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.TextPrimary
import com.mamm.mammapps.ui.viewmodel.AboutViewModel

@Composable
fun AboutScreen(
    viewModel: AboutViewModel = hiltViewModel()
) {
    val aboutInfo by viewModel.aboutInfo.collectAsStateWithLifecycle()
    val operatorLogo by viewModel.operatorLogo.collectAsStateWithLifecycle()
    val diagnosticState by viewModel.diagnosticUiState.collectAsStateWithLifecycle()
    val authTvCodeState by viewModel.authTvCodeUiState.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    var showAuthDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getAboutInfo()
        viewModel.getOperatorLogo()
    }

    LaunchedEffect(diagnosticState) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(authTvCodeState) {
        when (val state = authTvCodeState) {
            is UIState.Success -> {
                Toast.makeText(context, context.getString(R.string.auth_tv_code_success), Toast.LENGTH_SHORT).show()
                viewModel.resetAuthTvCodeState()
            }
            is UIState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetAuthTvCodeState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.paddingLarge)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        OperatorLogoImage(
            logoUrl = operatorLogo
        )

        Spacer(modifier = Modifier.height(24.dp))

        aboutInfo?.let { info ->
            Text(
                text = "${stringResource(R.string.username)}: ${info.userName}",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(Dimensions.paddingXSmall))
            Text(
                text = "IP: ${info.userIp}",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(Dimensions.paddingXSmall))
            Text(
                text = "${stringResource(R.string.application_version)} ${info.appVersion}",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // SECCIÓN DE DIAGNÓSTICO
        when (val state = diagnosticState) {
            is UIState.Idle -> {
                PrimaryButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .focusRequester(focusRequester),
                    onClick = { viewModel.runDiagnostic() },
                    text = stringResource(R.string.start_diagnostic))
            }

            is UIState.Loading -> {
                Spacer(modifier = Modifier.height(Dimensions.paddingXLarge))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(Dimensions.paddingXLarge))
                PrimaryButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .focusRequester(focusRequester),
                    onClick = { viewModel.runDiagnostic() },
                    text = stringResource(R.string.loading_diagnostic),
                    enabled = false)
            }

            is UIState.Success -> {
                DiagnosticResultsList(state.data as List<DownloadSpeedResult?>)
                Spacer(modifier = Modifier.height(16.dp))
                // Permitir repetir el test
                PrimaryButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .focusRequester(focusRequester),
                    onClick = { viewModel.runDiagnostic() },
                    text = stringResource(R.string.start_diagnostic))
            }

            is UIState.Error -> {
                Text(text = state.message, color = Color.Red)
                PrimaryButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .focusRequester(focusRequester),
                    onClick = { viewModel.runDiagnostic() },
                    text = stringResource(R.string.start_diagnostic))
            }
        }

        if (!LocalIsTV.current) {
            Spacer(modifier = Modifier.height(32.dp))
            when (authTvCodeState) {
                is UIState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                else -> {
                    PrimaryButton(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = { showAuthDialog = true },
                        text = stringResource(R.string.auth_tv_code_button)
                    )
                }
            }
        }
    }

    if (showAuthDialog) {
        PinDialog(
            onDismissRequest = { showAuthDialog = false },
            onConfirm = { pin ->
                showAuthDialog = false
                viewModel.authTvCode(pin)
            },
            title = stringResource(R.string.auth_tv_code_dialog_title),
            message = stringResource(R.string.auth_tv_code_dialog_message),
            isPassword = false
        )
    }

}
