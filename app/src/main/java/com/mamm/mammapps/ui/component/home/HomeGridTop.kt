package com.mamm.mammapps.ui.component.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mamm.mammapps.domain.model.metadata.Metadata
import com.mamm.mammapps.ui.component.metadata.DurationYearRatingRow
import com.mamm.mammapps.ui.constant.UIConstant
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.DetailInfoUI
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.HomeGridTopColor

@Composable
fun HomeGridTop(
    modifier: Modifier = Modifier,
    content: ContentEntityUI
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.86f)
                .align(Alignment.CenterEnd)
        ) {
            AsyncImage(
                model = content.horizontalImageUrl,
                contentDescription = content.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alignment = BiasAlignment(horizontalBias = 0f, verticalBias = -0.5f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RectangleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .align(Alignment.CenterEnd)
                .clip(RectangleShape)
                .background(
                    Brush.horizontalGradient(
                        colorStops = arrayOf(
                            // 0.0f (inicio) a 0.2f (20%) es negro sólido
                            0.0f to MaterialTheme.colorScheme.background,
                            0.17f to MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                            0.25f to MaterialTheme.colorScheme.background.copy(alpha = 0.9f),

                            // Transición rápida de negro a transparente entre el 20% y el 35%
                            0.35f to Color.Transparent,

                            // El resto (35% al 100%) es transparente
                            1.0f to Color.Transparent
                        )
                    )
                )
        )

        // Content overlay - Left side only (transparent background)
        Box(
            modifier = Modifier
                .height(260.dp)
                .fillMaxWidth(0.35f)
                .padding(Dimensions.paddingMedium),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimensions.paddingXSmall)
            ) {
                // Event title from ContentEntityUI
                if (content.title.isNotBlank()) {
                    Text(
                        text = content.title,
                        color = HomeGridTopColor.eventitle,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Separator line (only if title exists)
                if (content.detailInfo?.description?.isNotBlank() == true) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(1.dp)
                            .background(HomeGridTopColor.eventitle)
                    )
                }

                if (content.isLive()) {
                    EventStartEndDuration(
                        liveEventInfo = content.liveEventInfo,
                        duration = content.detailInfo?.metadata?.durationMin,
                    )
                } else {
                    content.detailInfo?.metadata?.let { metadata ->
                        DurationYearRatingRow(
                            metadata = metadata,
                            textcolor = HomeGridTopColor.metadata
                        )
                    }
                }

                // Event description from ContentEntityUI subtitle
                content.detailInfo?.description?.let { description ->
                    if (description.isNotBlank()) {
                        Text(
                            text = description,
                            color = HomeGridTopColor.description,
                            style = MaterialTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun HomeGridTopPreview() {
    MaterialTheme {
        HomeGridTop(
            content = ContentEntityUI(
                title = "Malas lenguas: Episodio 153",
                imageUrl = "https://picsum.photos/800/600?random=1",
                identifier = ContentIdentifier.VoD(1),
                detailInfo = DetailInfoUI(
                    description = "Jesús Cintora presenta este magazine de actualidad en el que, con humor, se desmontan bulos que circulan en los medios de comunicación y las redes sociales.",
                    metadata = Metadata(
                        actors = emptyList(),
                        director = "Denis Villeneuve",
                        year = "2021",
                        country = "Estados Unidos",
                        durationMin = "155",
                        ratingURL = null, // Ejemplo con valor nulo
                        genres = "Drama|Ciencia ficción",
                        originalTitle = "Dune"
                    )
                )
            )
        )
    }
}