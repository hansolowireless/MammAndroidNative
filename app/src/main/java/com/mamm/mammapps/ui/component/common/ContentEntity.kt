package com.mamm.mammapps.ui.component.common

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Glow
// Renombramos la importación para evitar conflictos de nombres
import androidx.tv.material3.Surface as TvSurface
import coil.compose.AsyncImage
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.component.common.contententity.SharedContentEntity
import com.mamm.mammapps.ui.extension.glow
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.theme.ContentEntityColor
import com.mamm.mammapps.ui.theme.Dimensions

@Composable
fun ContentEntity(
    modifier: Modifier = Modifier,
    contentEntityUI: ContentEntityUI,
    mostWatchedOrder: Int? = null,
    onClick: () -> Unit,
    onFocus: () -> Unit = {}
) {
    // Usamos LocalIsTV para decidir qué implementación renderizar
    if (LocalIsTV.current) {
        ContentEntityTV(
            modifier = modifier,
            contentEntityUI = contentEntityUI,
            mostWatchedOrder = mostWatchedOrder,
            onClick = onClick,
            onFocus = onFocus
        )
    } else {
        ContentEntityMobile(
            modifier = modifier,
            contentEntityUI = contentEntityUI,
            mostWatchedOrder = mostWatchedOrder,
            onClick = onClick
        )
    }
}

@Composable
private fun ContentEntityTV(
    modifier: Modifier = Modifier,
    contentEntityUI: ContentEntityUI,
    mostWatchedOrder: Int? = null,
    onClick: () -> Unit,
    onFocus: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val transition = updateTransition(isFocused, label = "focusTransition")
    val scale by transition.animateFloat(
        transitionSpec = { tween(200) },
        label = "scaleAnimation"
    ) { focused -> if (focused) 1f else 0.95f }

    val glowAlpha by transition.animateFloat(
        transitionSpec = { tween(200) },
        label = "glowAlpha"
    ) { focused -> if (focused && LocalIsTV.current) 0.6f else 0f }

    TvSurface(
        onClick = onClick,
        modifier = modifier
            .height(contentEntityUI.height)
            .aspectRatio(contentEntityUI.aspectRatio)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .glow(enabled = isFocused, alpha = glowAlpha, glowRadius = 20.dp)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                if (isFocused) onFocus()
            }
            .focusable(),
        shape = ClickableSurfaceDefaults.shape(
            shape = RoundedCornerShape(Dimensions.cornerRadius)
        ),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color.Transparent
        ),
//        glow = ClickableSurfaceDefaults.glow(
//            glow = Glow(
//                // Tu lógica de color dependiente del foco se mantiene aquí
//                elevationColor = if (isFocused) ContentEntityColor.glow else Color.Transparent,
//                elevation = 12.dp
//            )
//        )
    ) {
        SharedContentEntity(contentEntityUI, mostWatchedOrder)
    }
}

@Composable
private fun ContentEntityMobile(
    modifier: Modifier = Modifier,
    contentEntityUI: ContentEntityUI,
    mostWatchedOrder: Int? = null,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .height(contentEntityUI.height)
            .aspectRatio(contentEntityUI.aspectRatio),
        shape = RoundedCornerShape(Dimensions.cornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black) // Fondo mientras carga la imagen
    ) {
        // La Card ya se encarga de recortar el contenido (clip)
        SharedContentEntity(contentEntityUI, mostWatchedOrder)
    }
}

@Preview(name = "Móvil", showBackground = true)
@Composable
fun ContentEntityHorizontalPreviewMobile() {
    CompositionLocalProvider(LocalIsTV provides false) {
        ContentEntity(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentEntityUI = ContentEntityUI(
                title = "Título en Móvil",
                imageUrl = "https://picsum.photos/400/300",
                identifier = ContentIdentifier.VoD(2)
            ),
            onClick = { }
        )
    }
}

@Preview(name = "TV", device = "id:tv_1080p", showBackground = true)
@Composable
fun ContentEntityHorizontalPreviewTV() {
    CompositionLocalProvider(LocalIsTV provides true) {
        ContentEntity(
            modifier = Modifier
                .height(200.dp) // Damos una altura fija para el preview de TV
                .padding(16.dp),
            contentEntityUI = ContentEntityUI(
                title = "Título en TV",
                imageUrl = "https://picsum.photos/400/300",
                identifier = ContentIdentifier.VoD(2)
            ),
            onClick = { }
        )
    }
}
