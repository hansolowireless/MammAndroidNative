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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mamm.mammapps.data.model.metadata.Metadata
import com.mamm.mammapps.ui.component.metadata.DurationYearRatingRow
import com.mamm.mammapps.ui.constant.UIConstant
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.DetailInfoUI
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.HomeGridTopColor

@Composable
fun HomeGridTop(event: ContentEntityUI) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(UIConstant.HOMEGRIDTOP_IMAGE_WIDTH_FRACTION)
                .align(Alignment.CenterEnd)
        ) {
            AsyncImage(
                model = event.horizontalImageUrl,
                contentDescription = event.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(UIConstant.HOMEGRIDTOP_IMAGE_WIDTH_FRACTION)
                .align(Alignment.CenterEnd)
                .clip(RectangleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(UIConstant.HOMEGRIDTOP_IMAGE_WIDTH_FRACTION)
                .align(Alignment.CenterEnd)
                .clip(RectangleShape)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                            Color.Transparent,
                            Color.Transparent
                        )
                    )
                )
        )

        // Content overlay - Left side only (transparent background)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(1 - UIConstant.HOMEGRIDTOP_IMAGE_WIDTH_FRACTION)
                .padding(Dimensions.paddingMedium),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimensions.paddingXSmall)
            ) {
                // Event title from ContentEntityUI
                if (event.title.isNotBlank()) {
                    Text(
                        text = event.title,
                        color = HomeGridTopColor.eventitle,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Separator line (only if title exists)
                if (event.detailInfo?.description?.isNotBlank() == true) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(1.dp)
                            .background(HomeGridTopColor.eventitle)
                    )
                }

                if (event.isLive()) {
                    EventStartEndDuration(
                        liveEventInfo = event.liveEventInfo,
                        duration = event.detailInfo?.metadata?.durationMin,
                    )
                } else {
                    event.detailInfo?.metadata?.let { metadata ->
                        DurationYearRatingRow(
                            metadata = metadata,
                            textcolor = HomeGridTopColor.metadata
                        )
                    }
                }

                // Event description from ContentEntityUI subtitle
                event.detailInfo?.description?.let { description ->
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
            event = ContentEntityUI(
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