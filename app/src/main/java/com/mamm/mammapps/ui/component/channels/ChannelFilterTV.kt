package com.mamm.mammapps.ui.component.channels

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset

@Composable
fun ChannelFilterTV(
    modifier: Modifier = Modifier,
    availableGenres: Set<String>,
    selectedGenres: Set<String>,
    onSelectedGenresChanged: (Set<String>) -> Unit,
    onClearSearch: () -> Unit = {}
) {
    val allGenresLabel = stringResource(R.string.all)

    // Creamos la lista final de pestañas, empezando con "Todos"
    val genres = remember(availableGenres, allGenresLabel) {
        listOf(allGenresLabel) + availableGenres.toList().sorted()
    }

    // Mantenemos el índice de la pestaña seleccionada actualmente en el TabRow
    var selectedTabIndex by remember(selectedGenres, genres) {
        val selectedGenre = selectedGenres.firstOrNull()
        mutableIntStateOf(if (selectedGenre != null) genres.indexOf(selectedGenre) else 0)
    }

    if (genres.isNotEmpty()) {
        ProvideLazyListPivotOffset(parentFraction = 0.03f) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = modifier,
            ) {
                genres.forEachIndexed { index, genre ->
                    Tab(
                        selected = selectedGenres.contains(genre),
                        onFocus = {
                            selectedTabIndex = index
                            // Al hacer foco, actualizamos el género seleccionado.
                            // Esto crea un efecto de selección instantánea al navegar por las pestañas.
                            if (genre == allGenresLabel) {
                                onSelectedGenresChanged(emptySet())
                            } else {
                                onSelectedGenresChanged(setOf(genre))
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        colors = TabDefaults.pillIndicatorTabColors(
                            selectedContentColor = Color.White,
                            focusedContentColor = Color.White,
                            inactiveContentColor = Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        Text(text = genre)
                    }
                }

                Spacer(modifier = Modifier.width(1000.dp))
            }
        }
    }
}