package com.mamm.mammapps.ui.screen

import android.content.pm.ActivityInfo
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.dialog.SessionExpiredDialog
import com.mamm.mammapps.ui.component.player.PlayerLoadErrorOverlay
import com.mamm.mammapps.ui.component.player.PlayerLoadingOverlay
import com.mamm.mammapps.ui.extension.findActivity
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.model.player.PlayerErrorType
import com.mamm.mammapps.ui.model.uistate.PlayerUIState
import com.mamm.mammapps.ui.theme.SnackbarColor
import com.mamm.mammapps.ui.viewmodel.VideoPlayerViewModel

@Composable
fun VideoPlayerScreen(
    viewModel: VideoPlayerViewModel = hiltViewModel(),
    playedContent: ContentToPlayUI,
    onSessionExpired: () -> Unit
) {

    val context = LocalContext.current
    val view = LocalView.current
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val player by viewModel.player.collectAsStateWithLifecycle()
    val content by viewModel.content.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.initializeWithContent(content = playedContent)
        // Se llama una sola vez: la conmutación por canal la gestiona el flatMapLatest interno
        viewModel.observeTickers()
    }

    LaunchedEffect(content) {
        viewModel.observeLiveEvents()
        viewModel.updateChannelList()
    }

    // --- EFECTOS DE PANTALLA COMPLETA Y ORIENTACIÓN ---
    DisposableEffect(Unit) {
        val window = context.findActivity().window
        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        onDispose {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    DisposableEffect(Unit) {
        val activity = context.findActivity()
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }

    DisposableEffect(lifecycleOwner) {
        // PLAY/PAUSE del Player al poner la app en segundo plano o reanudarla
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.pausePlayer()
                }
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.playPlayer()
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.releaseVariables()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        PlayerView(
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel,
            player = player,
            content = content
        )

        // Carga de URLs (CLM/DRM) en curso: póster difuminado + spinner en vez de pantalla negra
        AnimatedVisibility(
            visible = playerState is PlayerUIState.Idle || playerState is PlayerUIState.Loading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            PlayerLoadingOverlay(
                modifier = Modifier.fillMaxSize(),
                imageUrl = content.imageUrl,
                title = content.title
            )
        }

        // Fallo al obtener las URLs (CLM/DRM): el player no arranca, error a pantalla completa
        if (playerState is PlayerUIState.LoadError) {
            PlayerLoadErrorOverlay(
                modifier = Modifier.fillMaxSize(),
                onRetry = { viewModel.retryLoad() },
                onClose = { backDispatcher?.onBackPressed() }
            )
        }

        (playerState as? PlayerUIState.Error)?.let { errorState ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                containerColor = SnackbarColor.containerColor,
                contentColor = SnackbarColor.contentColor,
            ) {
                Text(text = playerErrorMessage(errorState))
            }
        }

        if (playerState is PlayerUIState.Session) {
            SessionExpiredDialog(
                onConfirm = onSessionExpired
            )
        }
    }
}

/**
 * Mapea el tipo de error a un mensaje localizado. En errores transitorios que se
 * están reintentando, añade el contador con los segundos que faltan.
 */
@Composable
private fun playerErrorMessage(error: PlayerUIState.Error): String {
    val baseMessage = when (error.type) {
        PlayerErrorType.TRANSIENT -> stringResource(R.string.player_error_transient)
        PlayerErrorType.DRM -> stringResource(R.string.player_error_drm)
        PlayerErrorType.DECODER -> stringResource(R.string.player_error_decoder)
        PlayerErrorType.UNAVAILABLE -> stringResource(R.string.player_error_unavailable)
        PlayerErrorType.GENERIC -> stringResource(R.string.player_error_generic)
    }
    return error.retrySecondsRemaining?.let { seconds ->
        stringResource(R.string.player_error_retrying, baseMessage, seconds)
    } ?: baseMessage
}

