package com.mamm.mammapps.ui.component.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.bookmark.Recommended
import com.mamm.mammapps.ui.component.metadata.DurationYearRatingRow
import com.mamm.mammapps.ui.component.common.LoadingSpinner
import com.mamm.mammapps.ui.component.common.PrimaryButton
import com.mamm.mammapps.ui.component.metadata.ActorCard
import com.mamm.mammapps.ui.component.metadata.DirectorAndGenreRow
import com.mamm.mammapps.ui.mapper.toSimilarContentRow
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.SeasonUI
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.ui.theme.DetailColor
import com.mamm.mammapps.ui.theme.Dimensions
import kotlinx.coroutines.launch

@Composable
fun DetailTV(
    modifier: Modifier = Modifier,
    content: ContentEntityUI,
    similarContent: List<Recommended>?,
    showPlayButton: Boolean = true,
    seasonInfoUIState: UIState<List<SeasonUI>>?,
    onClickPlay: () -> Unit,
    onClickEpisode: (Int, Int) -> Unit,
    onSimilarContentClick: (Any) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        // Imagen de fondo
        AsyncImage(
            model = content.horizontalImageUrl,
            contentDescription = content.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Overlay gradient para mejor legibilidad del texto
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        0.0f to Color.Black,
                        1.0f to Color.Transparent
                    )
                )
        )

        // Contenido principal
        Row {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.5f)
                        .padding(Dimensions.paddingLarge),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.paddingXSmall)
                ) {

                    // Título
                    Text(
                        text = content.title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge,
                    )

                    // Metadata principal
                    content.detailInfo?.metadata?.let { metadata ->
                        DurationYearRatingRow(metadata = metadata)

                        DirectorAndGenreRow(metadata = metadata)

                        // Título original si es diferente
                        if (metadata.originalTitle.isNotEmpty() && metadata.originalTitle != content.title) {
                            Text(
                                text = stringResource(
                                    R.string.original_title_label,
                                    metadata.originalTitle
                                ),
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Descripción
                    content.detailInfo?.description?.let { description ->
                        Text(
                            text = description,
                            color = DetailColor.description,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

                    // Botón de reproducir, solo si no es serie
                    if (showPlayButton) {
                        PrimaryButton(
                            modifier = Modifier.fillMaxWidth(0.5f),
                            text = stringResource(R.string.play),
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = stringResource(R.string.play_icon_content_description),
                                    tint = Color.Black,
                                    modifier = Modifier.padding(end = Dimensions.paddingXSmall)
                                )
                            },
                            onClick = {
                                onClickPlay()
                            },
                            onRegainFocus = {
                                coroutineScope.launch { scrollState.animateScrollTo(0) }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

                    // Sección de reparto
                    content.detailInfo?.metadata?.let { metadata ->
                        if (metadata.actors.isNotEmpty()) {
                            Text(
                                text = stringResource(R.string.cast),
                                color = Color.White,
                                modifier = Modifier.padding(bottom = Dimensions.paddingSmall)
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
                            ) {
                                items(metadata.actors) { actor ->
                                    ActorCard(actor = actor)
                                }
                            }
                        }
                    }
                }

                similarContent?.let { it ->
                    SimilarContentRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimensions.paddingSmall),
                        content = it.toSimilarContentRow(),
                        onContentClicked = { content ->
                            //Buscamos el contenido que mandamos a la siguiente vista detalle
                            similarContent.find { it.id == content.identifier.id }?.let {
                                onSimilarContentClick(it)
                            }
                        }
                    )
                }
            }

            when (seasonInfoUIState) {
                is UIState.Success -> SeasonChaptersTV(
                    seasons = seasonInfoUIState.data,
                    onEpisodeClick = { seasonOrder, episodeId ->
                        onClickEpisode(seasonOrder, episodeId)
                    }
                )

                UIState.Loading -> LoadingSpinner()
                else -> return@Row
            }

        }
    }
}