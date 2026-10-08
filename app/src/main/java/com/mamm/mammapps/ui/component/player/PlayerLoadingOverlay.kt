package com.mamm.mammapps.ui.component.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.Primary

/**
 * Overlay de carga del player: póster del contenido difuminado y oscurecido de fondo,
 * spinner y título del contenido, mientras se resuelven las URLs de reproducción (CLM/DRM).
 * En dispositivos sin soporte de blur (API < 31) se muestra la imagen solo atenuada.
 * Incluye un botón de cerrar para poder salir si la carga se demora.
 */
@Composable
fun PlayerLoadingOverlay(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    title: String? = null,
    onClose: () -> Unit
) {
    Box(
        modifier = modifier.background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.35f,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(24.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                strokeWidth = 3.dp,
                color = Primary
            )
            if (!title.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(Dimensions.paddingMedium))
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = Dimensions.paddingLarge)
                )
            }
        }

        // Solo en móvil: en TV no se llega con el mando y se sale con el botón Atrás.
        // Mismo icono y esquina que el close_button de exoplayer_controls.xml
        if (!LocalIsTV.current) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(Dimensions.paddingMedium)
                    .size(50.dp)
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = stringResource(R.string.accessibility_close),
                    tint = Color.White
                )
            }
        }
    }
}
