package com.mamm.mammapps.ui.component.common.contententity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.theme.Dimensions

@Composable
fun SharedContentEntity(
    contentEntityUI: ContentEntityUI,
    mostWatchedOrder: Int?
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = contentEntityUI.imageUrl,
            contentDescription = contentEntityUI.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        mostWatchedOrder?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .background(Color.Black)
                    .padding(Dimensions.paddingXSmall)
            ) {
                Text(
                    text = it.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        if (contentEntityUI.title.isNotBlank())
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = contentEntityUI.height.value * 0.5f // Gradiente relativo
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
            if (contentEntityUI.detailInfo?.subtitle?.isNotBlank() == true) {
                Text(
                    text = contentEntityUI.detailInfo.subtitle,
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