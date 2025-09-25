package com.mamm.mammapps.ui.component.detail

import android.graphics.Paint.Align
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun SeasonTabs(seasons: List<SeasonUI>) {
    if (seasons.isEmpty()) {
        return
    }

    var selectedTabIndex by remember { mutableStateOf(0) }

    Column (modifier = Modifier.padding(horizontal = Dimensions.paddingMedium)) {

        Spacer(
            modifier = Modifier.height(Dimensions.paddingLarge)
        )

        TabRow(
            modifier = Modifier.height(50.dp),
            selectedTabIndex = selectedTabIndex
        ) {
            seasons.forEachIndexed { index, season ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    onFocus = { selectedTabIndex = index },
                    modifier = Modifier.clip(RectangleShape)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.padding(Dimensions.paddingSmall),
                            text = season.title,
                            style = MaterialTheme.typography.bodyLarge,
                            color = SeasonSelectorTabs.tabTitle
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(Dimensions.paddingSmall)
        )

        ProvideLazyListPivotOffset (parentFraction = 0.02f) {
            LazyColumn {
                items(seasons[selectedTabIndex].episodes) { episode ->
                    ContentEntityListItem(episode)
                }
                item {
                    Spacer(modifier = Modifier.height(500.dp))
                }
            }
        }
    }
}