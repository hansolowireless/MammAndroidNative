package com.mamm.mammapps.ui.component.channels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.component.common.PrimaryButton
import com.mamm.mammapps.ui.component.common.TextInput
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.MammAppsTheme

/**
 * Un componente de filtro dual que permite filtrar por nombre de canal y por género.
 *
 * @param modifier El modificador a aplicar al componente.
 * @param availableGenres La lista de todos los géneros disponibles para seleccionar.
 * @param selectedGenres Los géneros actualmente seleccionados.
 * @param onSelectedGenresChanged Lambda que se invoca cuando la selección de géneros cambia.
 * @param searchQuery El texto actual de búsqueda por nombre.
 * @param onSearchQueryChanged Lambda que se invoca cuando el texto de búsqueda cambia.
 */
@Composable
fun ChannelFilter(
    modifier: Modifier = Modifier,
    availableGenres: Set<String>,
    selectedGenres: Set<String>,
    onSelectedGenresChanged: (Set<String>) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onClearSearch: () -> Unit = {}
) {
    var showGenreDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = if (LocalIsTV.current) Dimensions.paddingSmall else 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PrimaryButton(
            modifier = if (LocalIsTV.current) Modifier.fillMaxWidth(0.2f) else Modifier,
            text = if (selectedGenres.isEmpty()) stringResource(id = R.string.all) else selectedGenres.first(),
            onClick = { showGenreDialog = true }
        )

        if (!LocalIsTV.current) {
            TextInput(
                modifier = Modifier.weight(1f),
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onSearchQueryChanged(it)
                },
                label = stringResource(id = R.string.search_channel_placeholder),
                keyboardType = KeyboardType.Text
            )
        }
        AnimatedVisibility(
            visible = searchQuery.isNotEmpty() || selectedGenres.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            IconButton(onClick = {
                searchQuery = ""
                onClearSearch()
            }) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = stringResource(id = R.string.accessibility_clear_search_action)
                )
            }
        }


    }

    // 3. Diálogo de selección de géneros
    if (showGenreDialog) {
        GenreSelectionDialog(
            allGenres = availableGenres,
            initiallySelectedGenres = selectedGenres,
            onDismiss = { showGenreDialog = false },
            onConfirm = { newSelectedGenres ->
                onSelectedGenresChanged(newSelectedGenres)
                showGenreDialog = false
            }
        )
    }
}

@Composable
private fun GenreSelectionDialog(
    allGenres: Set<String>,
    initiallySelectedGenres: Set<String>,
    onDismiss: () -> Unit,
    onConfirm: (Set<String>) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.filter_by_genre_title)) },
        text = {
            LazyColumn {
                items(allGenres.sorted()) { genre ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onConfirm(setOf(genre))
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = initiallySelectedGenres.contains(genre),
                            onCheckedChange = null // La lógica está en el `clickable` del Row
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = genre, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {
        },
        dismissButton = {
        }
    )
}

// --- Preview para ver cómo queda ---

@Preview(showBackground = true, name = "Channel Filter Mobile")
@Composable
private fun ChannelFilterPreview() {
    val allGenres = setOf("Cine", "Deportes", "Noticias", "Documentales", "Infantil", "Música")
    var selectedGenres by remember { mutableStateOf(setOf("Cine", "Deportes")) }
    var query by remember { mutableStateOf("Canal") }

    MammAppsTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ChannelFilter(
                availableGenres = allGenres,
                selectedGenres = selectedGenres,
                onSelectedGenresChanged = { selectedGenres = it },
                onSearchQueryChanged = { query = it }
            )
        }
    }
}

@Preview(showBackground = true, name = "Channel Filter Mobile (No selection)")
@Composable
private fun ChannelFilterNoSelectionPreview() {
    val allGenres = setOf("Cine", "Deportes", "Noticias", "Documentales", "Infantil", "Música")
    var selectedGenres by remember { mutableStateOf<Set<String>>(emptySet()) }
    var query by remember { mutableStateOf("") }

    MammAppsTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ChannelFilter(
                availableGenres = allGenres,
                selectedGenres = selectedGenres,
                onSelectedGenresChanged = { selectedGenres = it },
                onSearchQueryChanged = { query = it }
            )
        }
    }

}
