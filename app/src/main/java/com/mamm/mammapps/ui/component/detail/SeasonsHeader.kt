package com.mamm.mammapps.ui.component.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import com.mamm.mammapps.ui.model.SeasonUI
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.SeasonSelectorTabs

@Composable
fun SeasonsHeader(
    modifier: Modifier = Modifier,
    seasons: List<SeasonUI>,
    selectedTabIndex: Int,
    onClickTab: (Int) -> Unit,
    onFocusTab: (Int) -> Unit
) {
    TabRow(
        modifier = Modifier.height(50.dp),
        selectedTabIndex = selectedTabIndex
    ) {
        seasons.forEachIndexed { index, season ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onClickTab(index) },
                onFocus = { onFocusTab(index) },
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
}