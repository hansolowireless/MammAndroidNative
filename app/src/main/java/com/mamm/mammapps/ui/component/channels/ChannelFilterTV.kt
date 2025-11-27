package com.mamm.mammapps.ui.component.channels

import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowDefaults
import androidx.tv.material3.Text
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset

@Composable
fun ChannelFilterTV(
    modifier: Modifier = Modifier,
    availableGenres: Set<String>,
    selectedGenres: Set<String>,
    onSelectedGenresChanged: (Set<String>) -> Unit
) {
    val allGenresLabel = stringResource(R.string.all)
    val genres = remember(availableGenres, allGenresLabel) {
        listOf(allGenresLabel) + availableGenres.toList()
    }
    val tabFocusRequesters = remember(genres) {
        genres.map { FocusRequester() }
    }

    var selectedTabIndex by remember(selectedGenres, genres) {
        val selectedGenre = selectedGenres.firstOrNull()
        val index = if (selectedGenre != null) genres.indexOf(selectedGenre) else 0
        mutableIntStateOf(if (index != -1) index else 0)
    }

    if (genres.isNotEmpty()) {
        ProvideLazyListPivotOffset(parentFraction = 0.03f) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = modifier
                    .focusRestorer(tabFocusRequesters[selectedTabIndex])
                    .focusGroup()
            ) {
                genres.forEachIndexed { index, genre ->
                    val isSelected = (genre == allGenresLabel && selectedGenres.isEmpty()) || selectedGenres.contains(genre)

                    Tab(
                        selected = isSelected,
                        onFocus = {
                            selectedTabIndex = index
                            if (genre == allGenresLabel) {
                                onSelectedGenresChanged(emptySet())
                            } else {
                                onSelectedGenresChanged(setOf(genre))
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .focusRequester(tabFocusRequesters[index]),
                        colors = TabDefaults.pillIndicatorTabColors(
                            focusedContentColor = Color.White,
                            selectedContentColor = Color.White.copy(alpha = 0.85f),
                            inactiveContentColor = Color.White.copy(alpha = 1f),
                            disabledContentColor = Color.White.copy(alpha = 1f),
                            disabledInactiveContentColor = Color.White,
                            contentColor = Color.White,
                        )
                    ) {
                        Text(text = genre,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.width(1000.dp))
            }
        }
    }
}
