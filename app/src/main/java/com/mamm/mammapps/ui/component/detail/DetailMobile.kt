package com.mamm.mammapps.ui.component.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.metadata.Metadata
import com.mamm.mammapps.ui.component.common.ContentEntityListItem
import com.mamm.mammapps.ui.component.common.ExpandableText
import com.mamm.mammapps.ui.component.common.LoadingSpinner
import com.mamm.mammapps.ui.component.metadata.DirectorAndGenreRow
import com.mamm.mammapps.ui.component.metadata.DurationYearRatingRow
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.DetailInfoUI
import com.mamm.mammapps.ui.model.SeasonUI
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.ui.theme.DetailColor
import com.mamm.mammapps.ui.theme.Dimensions
import kotlinx.coroutines.flow.merge

@Composable
fun DetailMobile(
    modifier: Modifier = Modifier,
    content: ContentEntityUI,
    onClickPlay: () -> Unit,
    seasonInfoUIState: UIState<List<SeasonUI>>? = null,
    onClickEpisode: (Int, Int) -> Unit,
    onClose: () -> Unit
) {

    var selectedTabIndex by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = modifier
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = content.horizontalImageUrl, // CAMBIO: Usar horizontalImageUrl
                    contentDescription = content.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f),
                    contentScale = ContentScale.Crop
                )

                if (content.identifier !is ContentIdentifier.Serie){
                    IconButton(
                        onClick = onClickPlay,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(R.string.play),
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Dimensions.paddingSmall)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.accessibility_close),
                        tint = Color.White
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier.padding(Dimensions.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
            ) {
                Text(
                    text = content.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = DetailColor.title
                )

                content.detailInfo?.metadata?.let {
                    DirectorAndGenreRow(
                        metadata = it
                    )
                    DurationYearRatingRow(
                        metadata = it
                    )
                }

                content.detailInfo?.let { details ->
                    ExpandableText(
                        text = details.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DetailColor.description,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }


        when (seasonInfoUIState) {
            is UIState.Success -> {
                item {
                    SeasonsHeaderMobile(
                        seasons = seasonInfoUIState.data,
                        selectedTabIndex = selectedTabIndex,
                        onClickTab = {
                            selectedTabIndex = it
                        }
                    )
                }
                items(seasonInfoUIState.data[selectedTabIndex].episodes) { episode ->
                    ContentEntityListItem(
                        content = episode,
                        onClick = {
                            val currentSeason = seasonInfoUIState.data[selectedTabIndex]
                            onClickEpisode(currentSeason.order, episode.identifier.id)
                        }
                    )
                }
            }

            UIState.Loading -> item {
                LoadingSpinner()
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun DetailMobilePreview() {
    val sampleContent = ContentEntityUI(
        identifier = ContentIdentifier.VoD(10),
        imageUrl = "",
        horizontalImageUrl = "",
        title = "Título de la Película de Ejemplo",
        detailInfo = DetailInfoUI(
            description = "Esta es una descripción larga de la película de ejemplo para demostrar cómo se ajusta el texto y cómo funciona el scroll dentro de la pantalla de detalle. El contenido debería poder desplazarse verticalmente si no cabe en la pantalla.",
            metadata = com.mamm.mammapps.data.model.metadata.Metadata(
                actors = emptyList(),
                director = "Director de Ejemplo",
                year = "2023",
                country = "País de Ejemplo",
                durationMin = "120",
                ratingURL = null,
                genres = "Género de Ejemplo",
                originalTitle = "Título Original"
            )
        )
    )

    MaterialTheme {
        DetailMobile(
            modifier = Modifier.fillMaxWidth(),
            content = sampleContent,
            onClickPlay = {},
            onClickEpisode = { _, _ -> },
            onClose = {}
        )
    }
}
