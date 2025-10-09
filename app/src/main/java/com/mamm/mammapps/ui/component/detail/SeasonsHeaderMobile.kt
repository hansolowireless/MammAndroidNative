package com.mamm.mammapps.ui.component.detail

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.ui.model.SeasonUI
import com.mamm.mammapps.ui.theme.Dimensions

@Composable
fun SeasonsHeaderMobile(
    modifier: Modifier = Modifier,
    seasons: List<SeasonUI>,
    selectedTabIndex: Int,
    onClickTab: (Int) -> Unit
) {
    if (seasons.isEmpty()) {
        return
    }

    TabRow(
        modifier = modifier.height(50.dp),
        selectedTabIndex = selectedTabIndex,
        // Puedes personalizar los colores del indicador y el divisor aquí si lo necesitas
        // indicator = { ... }
        // divider = { ... }
    ) {
        seasons.forEachIndexed { index, season ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onClickTab(index) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Text(
                    modifier = Modifier.padding(Dimensions.paddingSmall),
                    text = season.title,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

