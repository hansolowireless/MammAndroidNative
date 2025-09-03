package com.mamm.mammapps.ui.component.common

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.util.AppConstants.Companion.HORIZONTAL_ASPECT_RATIO

@Composable
fun ContentEntityHorizontal(
    modifier: Modifier = Modifier,
    contentEntityUI: ContentEntityUI,
    onTap: () -> Unit,
    onFocusChanged: ((Boolean) -> Unit)? = null,
    isTv: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val transition = updateTransition(isFocused, label = "focusTransition")

    val scale by transition.animateFloat(
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioMediumBouncy) },
        label = "scale"
    ) { focused -> if (focused && isTv) 1.1f else 1f }

    val elevation by transition.animateDp(
        transitionSpec = { tween(200) },
        label = "elevation"
    ) { focused -> if (focused && isTv) 16.dp else 0.dp }

    Surface(
        modifier = modifier
            .width(180.dp)
            .aspectRatio(HORIZONTAL_ASPECT_RATIO)
            .scale(scale)
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                onFocusChanged?.invoke(focusState.isFocused)
            }
            .focusable(enabled = isTv)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onTap() },
        shape = RoundedCornerShape(10.dp),
        tonalElevation = elevation,
        shadowElevation = elevation,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier.clip(RoundedCornerShape(10.dp))
        ) {
            AsyncImage(
                model = contentEntityUI.imageUrl,
                contentDescription = contentEntityUI.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

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
                    .padding(16.dp),
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
        ContentEntityHorizontal(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentEntityUI = ContentEntityUI(
                title = "Título de ejemplo",
                subtitle = "Subtítulo descriptivo",
                imageUrl = "https://picsum.photos/400/300"
            ),
            onTap = {  }
        )
    }
}
