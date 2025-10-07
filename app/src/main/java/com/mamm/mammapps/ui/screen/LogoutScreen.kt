package com.mamm.mammapps.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.ui.viewmodel.LogoutViewModel

@Composable
fun LogoutScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: LogoutViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect (uiState)
    {
        when (uiState) {
            is UIState.Success<*> -> onNavigateToLogin()
            is UIState.Loading -> {viewModel.logout()}
            else -> {}
        }
    }

}