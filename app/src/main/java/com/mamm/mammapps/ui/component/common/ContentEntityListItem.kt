package com.mamm.mammapps.ui.component.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import coil.compose.AsyncImage
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.ContentListUI
import com.mamm.mammapps.ui.model.DetailInfoUI
import com.mamm.mammapps.ui.theme.ContentEntityListItemColor
import com.mamm.mammapps.ui.theme.Dimensions
import drawable.Clock

@Composable
fun ContentEntityListItem(
    modifier : Modifier = Modifier,
    content: ContentListUI,
    channelInfo: ContentEntityUI? = null,
    showLiveIndicator: Boolean = false,
    showCatchupIndicator: Boolean = false,
    onClick: () -> Unit = {}
) {
    var isFocused by remember { mutableStateOf(false) }

    ListItem(
        selected = false,
        onClick = onClick,
        colors = ListItemDefaults.colors(
            containerColor = ContentEntityListItemColor.unfocusedContent,
            focusedContainerColor = ContentEntityListItemColor.focusedContent,
        ),
        modifier = modifier
            .onFocusChanged { isFocused = it.isFocused }
            .clickable(onClick = onClick),
        leadingContent = channelInfo?.let {
            {
                AsyncImage(
                    model = it.detailInfo?.squareLogo,
                    contentDescription = it.title,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        },
        headlineContent = {
            Row {
                Text(
                    modifier = Modifier.weight(1f),
                    text = content.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = if (isFocused) 2 else 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (showLiveIndicator) {
                    Text(
                        text = stringResource(R.string.live),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(
                                color = Color.Red,
                                shape = RectangleShape
                            )
                            .padding(horizontal = 2.dp)
                            .weight(0.4f)
                    )
                } else if (showCatchupIndicator) {
                    Icon(
                        imageVector = Clock,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
            }
        },
        supportingContent = if (isFocused || showLiveIndicator) {
            content.detailInfo?.description?.let { description ->
                if (description.isNotBlank()) {
                    {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else null
            }
        } else null,
        trailingContent = content.imageUrl.let { imageUrl ->
            {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = content.detailInfo?.description,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ContentEntityListItemPreview() {
    // Datos de ejemplo para la preview
    val sampleContent = ContentListUI(
        identifier = ContentIdentifier.Event(1),
        title = "Título del Contenido Muy Largo Que Debería Cortarse",
        imageUrl = "https://via.placeholder.com/150", // URL de imagen de ejemplo
        detailInfo = DetailInfoUI(
            description = "Esta es una descripción detallada del contenido que aparecerá cuando el elemento esté enfocado. Proporciona más contexto sobre lo que el usuario está a punto de ver."
        ),
    )

    Column(modifier = Modifier.padding(16.dp)) {
        // --- Preview del estado normal (sin foco) ---
        Text(text = "Estado Normal (Sin Foco):", style = MaterialTheme.typography.labelMedium, color = Color.White)
        ContentEntityListItem(
            content = sampleContent,
            onClick = {}
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Preview del estado con foco (simulado) ---
        // Para simular el foco, podrías crear una versión modificada
        // o simplemente documentar que el estado 'isFocused' interno lo maneja.
        // Aquí lo mostramos con el indicador de "En Vivo".
        Text(text = "Estado con Foco y 'En Vivo':", style = MaterialTheme.typography.labelMedium, color = Color.White)
        ContentEntityListItem(
            content = sampleContent,
            showLiveIndicator = true,
            onClick = {}
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Preview con indicador de Catch-up ---
        Text(text = "Estado con Indicador de Catch-up:", style = MaterialTheme.typography.labelMedium, color = Color.White)
        ContentEntityListItem(
            content = sampleContent,
            showCatchupIndicator = true,
            onClick = {}
        )
    }
}
