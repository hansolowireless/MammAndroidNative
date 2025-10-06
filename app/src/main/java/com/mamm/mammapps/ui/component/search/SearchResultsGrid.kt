package com.mamm.mammapps.ui.component.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.ui.component.common.ContentEntity
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.theme.Dimensions

@Composable
fun SearchResultsGrid(
    results: List<ContentEntityUI>,
    onContentClick: (ContentEntityUI) -> Unit
) {
    LazyVerticalGrid (
        columns = GridCells.Fixed(5),
        contentPadding = PaddingValues(top = Dimensions.paddingSmall, start = Dimensions.paddingSmall, end = Dimensions.paddingSmall),
        verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingXSmall)
    ) {
        items(results, key = { it.identifier.id }) { content ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.paddingXSmall),
                contentAlignment = Alignment.Center
            ) {
                ContentEntity(
                    contentEntityUI = content,
                    onClick = { onContentClick(content) }
                )
            }
        }
    }
}