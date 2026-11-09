package com.mamm.mammapps.ui.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.common.PrimaryButton
import com.mamm.mammapps.ui.theme.Primary

/**
 * Diálogo de error del player, sobre AlertDialog y PrimaryButton como SessionExpiredDialog.
 * Se usa cuando falla la obtención de las URLs (CLM/DRM) y cuando se agotan los reintentos
 * automáticos del player. Las acciones opcionales se ocultan si se pasan a null.
 * @param onRetry reintentar; null en errores en los que reintentar no sirve.
 * @param onShowChannels abrir la lista de canales (zapping); null si no se reproduce un canal.
 * @param onClose salir del player; también se dispara con Atrás.
 * Los botones van de menor a mayor importancia (Cerrar, Canales, Reintentar) y el foco inicial
 * se pone en el último, la acción principal, para el manejo con mando (TV).
 */
@Composable
fun PlayerErrorDialog(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.player_error_load),
    onRetry: (() -> Unit)?,
    onShowChannels: (() -> Unit)? = null,
    onClose: () -> Unit
) {
    val actions = listOfNotNull(
        DialogAction(stringResource(R.string.player_error_close), onClose),
        onShowChannels?.let { DialogAction(stringResource(R.string.player_error_channels), it) },
        onRetry?.let { DialogAction(stringResource(R.string.player_error_retry), it) }
    )
    val initialFocusRequester = remember { FocusRequester() }

    AlertDialog(
        modifier = modifier.width(dialogWidthFor(actions.size)),
        onDismissRequest = onClose,
        properties = DialogProperties(dismissOnClickOutside = false, usePlatformDefaultWidth = false),
        shape = RoundedCornerShape(20.dp),
        containerColor = ContainerColor,
        icon = {
            // Icono en un círculo tenue con el color de marca
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Primary.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        text = {
            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        // Todos los botones en un mismo Row centrado en vez de los slots confirm/dismiss,
        // que los alinean a la derecha con una separación mínima
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ButtonSpacing, Alignment.CenterHorizontally)
            ) {
                actions.forEachIndexed { index, action ->
                    DialogButton(
                        text = action.label,
                        onClick = action.onClick,
                        focusRequester = initialFocusRequester.takeIf { index == actions.lastIndex }
                    )
                }
            }
        }
    )
}

private data class DialogAction(val label: String, val onClick: () -> Unit)

@Composable
private fun DialogButton(
    text: String,
    onClick: () -> Unit,
    focusRequester: FocusRequester?
) {
    PrimaryButton(
        text = text,
        onClick = onClick,
        modifier = Modifier
            .width(ButtonWidth)
            .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier)
    )
    // Dentro del contenido del diálogo, que se compone en su propia ventana: desde fuera
    // el FocusRequester aún no estaría enganchado al pedir el foco
    if (focusRequester != null) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

// Lo justo para que quepan los botones, sin bajar de un mínimo. AlertDialog no pasa de 560dp,
// así que con tres botones ButtonWidth y ButtonSpacing tienen que caber en ese máximo.
private fun dialogWidthFor(buttonCount: Int): Dp =
    maxOf(
        MinDialogWidth,
        ButtonWidth * buttonCount + ButtonSpacing * (buttonCount - 1) + DialogHorizontalPadding * 2
    )

private val MinDialogWidth = 440.dp
// Padding horizontal interno de AlertDialog
private val DialogHorizontalPadding = 24.dp
private val ContainerColor = Color(0xFF1E1E1E)

// Ancho fijo: en TV PrimaryButton rellena todo el espacio disponible y el primer botón
// se comería el ancho entero dejando fuera a los demás
private val ButtonWidth = 150.dp

// Holgada: en TV el botón con foco se agranda y se come parte del hueco
private val ButtonSpacing = 24.dp
