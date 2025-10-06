package com.mamm.mammapps.ui.screen

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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.mamm.mammapps.R
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.ui.component.common.DurationYearRatingRow
import com.mamm.mammapps.ui.component.common.LoadingSpinner
import com.mamm.mammapps.ui.component.common.PrimaryButton
import com.mamm.mammapps.ui.component.detail.SeasonTabs
import com.mamm.mammapps.ui.component.detail.SimilarContentRow
import com.mamm.mammapps.ui.component.metadata.ActorCard
import com.mamm.mammapps.ui.mapper.toSimilarContentRow
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.viewmodel.DetailViewModel
import kotlinx.coroutines.launch


@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    content: ContentEntityUI,
    prefoundContent: Any? = null,
    routeTag: AppRoute,
    onClickPlay: (Any) -> Unit,
    onSimilarContentClick: (Any) -> Unit
) {

    val seasonInfoUIState by viewModel.seasonInfoUIState.collectAsStateWithLifecycle()
    val clickedContent by viewModel.clickedContent.collectAsStateWithLifecycle()
    val similarContent by viewModel.similarContent.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(clickedContent) {
        clickedContent?.let(onClickPlay)
    }

    LaunchedEffect(Unit) {
        scrollState.scrollTo(0)
    }

    LaunchedEffect(Unit) {
        viewModel.getSeasonInfo(content)
    }

    LaunchedEffect(content) {
        viewModel.getSimilar(content.detailInfo?.subgenreId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearClickedContent()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        text = content.title.orEmpty(),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge,
                    )

                    // Metadata principal
                    content.detailInfo?.metadata?.let { metadata ->
                        DurationYearRatingRow(metadata = metadata)

                        // Director y género
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingXSmall)
                        ) {
                            if (metadata.director.isNotBlank()) {
                                Text(
                                    text = stringResource(
                                        R.string.director_label,
                                        metadata.director
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = stringResource(R.string.separator),
                                    color = Color.White
                                )
                            }
                            Text(
                                text = metadata.genres,
                                color = Color.White
                            )
                        }

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
                            color = Color.White,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

                    // Botón de reproducir, solo si no es serie
                    if (content.identifier !is ContentIdentifier.Serie) {
                        PrimaryButton(
                            text = stringResource(R.string.play),
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = stringResource(R.string.play_icon_content_description),
                                    tint = Color.White,
                                    modifier = Modifier.padding(end = Dimensions.paddingXSmall)
                                )
                            },
                            onClick = {

                                if (prefoundContent != null) {
                                    onClickPlay(prefoundContent)
                                    return@PrimaryButton
                                }

                                viewModel.findContent(
                                    entityUI = content,
                                    routeTag = routeTag
                                )
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
                            similarContent?.find { it.id == content.identifier.id }?.let {
                                onSimilarContentClick(it)
                            }
                        }
                    )
                }
            }

            when (val seasonInfoState = seasonInfoUIState) {
                is UIState.Success -> SeasonTabs(
                    seasons = seasonInfoState.data,
                    onEpisodeClick = { seasonOrder, episodeId ->
                        viewModel.findEpisode(seasonOrder, episodeId)
                    }
                )

                UIState.Loading -> LoadingSpinner()
                else -> return@Row
            }

        }
    }
}

