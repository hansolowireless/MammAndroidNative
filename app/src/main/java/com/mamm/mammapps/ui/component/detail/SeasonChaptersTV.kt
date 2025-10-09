package com.mamm.mammapps.ui.component.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import com.mamm.mammapps.ui.component.common.ContentEntityListItem
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.model.SeasonUI
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.SeasonSelectorTabs

@Composable
fun SeasonChaptersTV(
    seasons: List<SeasonUI>,
    onEpisodeClick: (Int, Int) -> Unit
) {
    if (seasons.isEmpty()) {
        return
    }

    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier.padding(horizontal = Dimensions.paddingMedium)) {

        Spacer(
            modifier = Modifier.height(Dimensions.paddingLarge)
        )

        SeasonsHeader(
            seasons = seasons,
            selectedTabIndex = selectedTabIndex,
            onClickTab = { selectedTabIndex = it },
            onFocusTab = { selectedTabIndex = it }
        )

        Spacer(
            modifier = Modifier.height(Dimensions.paddingSmall)
        )

        ProvideLazyListPivotOffset(parentFraction = 0.02f) {
            LazyColumn {
                items(seasons[selectedTabIndex].episodes) { episode ->
                    ContentEntityListItem(
                        content = episode,
                        onClick = {
                            onEpisodeClick(seasons[selectedTabIndex].order, episode.identifier.id)
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(500.dp))
                }
            }
        }
    }
}