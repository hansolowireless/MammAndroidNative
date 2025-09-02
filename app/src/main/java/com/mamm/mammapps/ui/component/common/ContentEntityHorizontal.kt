package com.mamm.mammapps.ui.component.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    onContentEntityClick: () -> Unit
) {
    Box(
        modifier = modifier
            .width(200.dp)
            .aspectRatio(HORIZONTAL_ASPECT_RATIO)
            .clip(RoundedCornerShape(10.dp)),
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
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        ),
                        startY = 0.5f
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(BottomCenter)
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
            Text(
                text = contentEntityUI.subtitle ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
            onContentEntityClick = {  }
        )
    }
}
