package com.mamm.mammapps.ui.component.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.common.PrimaryButton
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.Primary

/**
 * Overlay de error cuando falla la obtención de las URLs de reproducción (CLM/DRM)
 * y el player no llega a arrancar: mensaje y acciones de reintentar o salir.
 * El foco inicial se pone en «Reintentar» para el manejo con mando (TV).
 */
@Composable
fun PlayerLoadErrorOverlay(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    val retryFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        retryFocusRequester.requestFocus()
    }

    Box(
        modifier = modifier.background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))
            Text(
                text = stringResource(R.string.player_error_load),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Dimensions.paddingLarge)
            )
            Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
            Row(horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium)) {
                PrimaryButton(
                    text = stringResource(R.string.player_error_retry),
                    onClick = onRetry,
                    modifier = Modifier.focusRequester(retryFocusRequester)
                )
                PrimaryButton(
                    text = stringResource(R.string.player_error_close),
                    onClick = onClose
                )
            }
        }
    }
}
