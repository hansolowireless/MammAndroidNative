package com.mamm.mammapps.ui.component.common

// --- INICIO DE CAMBIOS ---
// 1. Importa AMBOS botones, dándoles un alias al de TV para evitar conflictos.
// --- FIN DE CAMBIOS ---
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.theme.ButtonColor
import androidx.compose.material3.Button as MobileButton
import androidx.compose.material3.ButtonDefaults as MobileButtonDefaults
import androidx.compose.material3.MaterialTheme as MobileMaterialTheme
import androidx.compose.material3.Text as MobileText
import androidx.tv.material3.Button as TvButton
import androidx.tv.material3.ButtonDefaults as TvButtonDefaults
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.Text as TvText

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    height: Dp = if (LocalIsTV.current) 40.dp else 48.dp,
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    onRegainFocus: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isFocused) {
        if (isFocused) onRegainFocus?.invoke()
    }

    if (LocalIsTV.current) {
        TvButton(
            onClick = onClick,
            shape = TvButtonDefaults.shape(shape = RoundedCornerShape(8.dp)),
            modifier = modifier
                .height(height),
            glow = TvButtonDefaults.glow(),
            colors = TvButtonDefaults.colors(
                containerColor = ButtonColor.background,
                focusedContainerColor = ButtonColor.background,
                contentColor = ButtonColor.unfocusedContent,
                focusedContentColor = ButtonColor.focusedContent,
                disabledContainerColor = ButtonColor.background.copy(alpha = 0.5f),
                disabledContentColor = ButtonColor.unfocusedContent.copy(alpha = 0.5f)
            ),
            interactionSource = interactionSource,
            enabled = enabled
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    icon()
                }
                TvText(
                    text = text,
                    style = TvMaterialTheme.typography.bodyLarge
                )
            }
        }
    } else {
        // --- CÓDIGO NUEVO PARA MÓVIL/TABLET ---
        MobileButton(
            onClick = onClick,
            shape = RectangleShape, // En móvil es más directo
            modifier = modifier
                .height(height),
            colors = MobileButtonDefaults.buttonColors(
                containerColor = ButtonColor.background,
                contentColor = ButtonColor.focusedContent,
                disabledContainerColor = ButtonColor.background.copy(alpha = 0.5f),
                disabledContentColor = ButtonColor.focusedContent.copy(alpha = 0.5f)
            ),
            interactionSource = interactionSource,
            enabled = enabled
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    icon()
                }
                MobileText(
                    text = text,
                    style = MobileMaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}


@Preview
@Composable
fun PrimaryButtonPreview() {
    // Para el preview, es bueno envolverlo en el tema de móvil
    MobileMaterialTheme {
        PrimaryButton(text = "Play", onClick = {})
    }
}
