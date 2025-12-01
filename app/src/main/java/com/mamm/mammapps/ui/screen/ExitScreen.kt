package com.mamm.mammapps.ui.screen

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.ui.component.dialog.ExitDialog
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.ui.viewmodel.LogoutViewModel
import kotlin.system.exitProcess

@Composable
fun ExitScreen(
    viewModel: LogoutViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    when (uiState) {
        is UIState.Loading -> {
            ExitDialog(
                onDismissRequest = {
                    backDispatcher?.onBackPressed()
                },
                onConfirmation = {
                    exitProcess(0)
                }
            )
        }

        else -> {}
    }

}