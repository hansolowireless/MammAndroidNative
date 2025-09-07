package com.mamm.mammapps.ui.component.common

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.size.Dimension
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.extension.glow
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.Primary

@Composable
fun ContentEntity(
    modifier: Modifier = Modifier,
    contentEntityUI: ContentEntityUI,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val transition = updateTransition(isFocused, label = "focusTransition")

    val glowAlpha by transition.animateFloat(
        transitionSpec = { tween(200) },
        label = "glowAlpha"
    ) { focused -> if (focused && LocalIsTV.current) 0.8f else 0f }

    Surface(
        modifier = modifier
            .height(contentEntityUI.height)
            .aspectRatio(contentEntityUI.aspectRatio)
            .graphicsLayer {
                scaleX = if (isFocused) 0.98f else 0.92f
                scaleY = if (isFocused) 0.98f else 0.92f
            }
            .glow(enabled = isFocused, alpha = glowAlpha)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            }
            .focusable(enabled = LocalIsTV.current)
            .onKeyEvent {
                if (it.key == Key.DirectionCenter && it.type == KeyEventType.KeyDown) {
                    onClick()
                    return@onKeyEvent true
                }
                false
            },
        shape = RoundedCornerShape(Dimensions.cornerRadius),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier.clip(RoundedCornerShape(Dimensions.cornerRadius))
        ) {
            AsyncImage(
                model = contentEntityUI.imageUrl,
                contentDescription = contentEntityUI.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            if (contentEntityUI.title.isNotBlank())
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                startY = 0.5f
                            )
                        )
                )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(Dimensions.paddingMedium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = contentEntityUI.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!contentEntityUI.subtitle.isNullOrEmpty()) {
                    Text(
                        text = contentEntityUI.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ContentEntityHorizontalPreview() {
    CompositionLocalProvider {
        ContentEntity(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentEntityUI = ContentEntityUI(
                title = "Título de ejemplo",
                subtitle = "Subtítulo descriptivo",
                imageUrl = "https://picsum.photos/400/300",
                identifier = ContentIdentifier.VoD("2")
            ),
            onClick = { }
        )
    }
}
