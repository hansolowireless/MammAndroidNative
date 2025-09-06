package com.mamm.mammapps.ui.component

import android.provider.SyncStateContract
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.util.AppConstants

@Composable
fun HomeGridTop(event: ContentEntityUI) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {

        // Background image covering the right half
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(AppConstants.HOMEGRIDTOP_IMAGE_WIDTH_FRACTION)
                .align(Alignment.CenterEnd)
        ) {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Horizontal gradient overlay - only over the image area (right half)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(AppConstants.HOMEGRIDTOP_IMAGE_WIDTH_FRACTION)
                .align(Alignment.CenterEnd)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                            Color.Transparent
                        ),
                        startX = 0f,
                        endX = 200f
                    )
                )
        )

        // Vertical gradient overlay - bottom to top over the image area
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(AppConstants.HOMEGRIDTOP_IMAGE_WIDTH_FRACTION)
                .align(Alignment.CenterEnd)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.background
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // Content overlay - Left side only (transparent background)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.6f)
                .padding(Dimensions.paddingMedium),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium)
            ) {
                // Event title from ContentEntityUI
                if (event.title.isNotBlank()) {
                    Text(
                        text = event.title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Separator line (only if title exists)
                if (event.title.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(1.dp)
                            .background(Color.White)
                    )
                }

                // Event description from ContentEntityUI subtitle
                event.subtitle?.let { subtitle ->
                    if (subtitle.isNotBlank()) {
                        Text(
                            text = subtitle,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeGridTopPreview() {
    CompositionLocalProvider {
        HomeGridTop(
            event = ContentEntityUI(
                title = "Malas lenguas: Episodio 153",
                subtitle = "Jesús Cintora presenta este magazine de actualidad en el que, con humor, se desmontan bulos que circulan en los medios de comunicación y las redes sociales.",
                imageUrl = "https://picsum.photos/800/600?random=1",
                identifier = ContentIdentifier.VoD("1")
            )
        )
    }
}