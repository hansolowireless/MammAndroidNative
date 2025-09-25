package com.mamm.mammapps.ui.component.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import com.mamm.mammapps.ui.model.SeasonUI

@Composable
fun SeasonTabs(seasons: List<SeasonUI>) {
    if (seasons.isEmpty()) {
        return
    }

    var selectedTabIndex by remember { mutableStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            seasons.forEachIndexed { index, season ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    onFocus = { selectedTabIndex = index }
                ) {
                    Text(text = season.title)
                }
            }
        }

        val currentEpisodes = seasons[selectedTabIndex].episodes

    }
}