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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.Actor
import com.mamm.mammapps.ui.component.common.DurationYearRatingRow
import com.mamm.mammapps.ui.component.common.PrimaryButton
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.theme.Dimensions

@Composable
fun DetailScreen(
    content: ContentEntityUI,
    onPlayClick: () -> Unit
) {

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        scrollState.scrollTo(0) // Fuerza el scroll al inicio
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de fondo
        AsyncImage(
            model = content.imageUrl,
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
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .padding(Dimensions.paddingLarge)
                .verticalScroll(scrollState),
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

                // Director y género
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingXSmall)
                ) {
                    Text(
                        text = stringResource(R.string.director_label, metadata.director),
                        color = Color.White
                    )

                    Text(
                        text = stringResource(R.string.separator),
                        color = Color.White
                    )

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

                // Disponible hasta (si hay fecha)
                Text(
                    text = stringResource(R.string.available_until_label, "01-01-2026"),
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // Descripción
            content.detailInfo?.description?.let { description ->
                Text(
                    text = description,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Botón de reproducir
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
                onClick = onPlayClick
            )

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
    }
}

@Composable
fun ActorCard(actor: Actor) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        // Imagen del actor
        AsyncImage(
            model = actor.image,
            contentDescription = actor.name,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(Dimensions.paddingXSmall))

        Text(
            text = actor.name,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
