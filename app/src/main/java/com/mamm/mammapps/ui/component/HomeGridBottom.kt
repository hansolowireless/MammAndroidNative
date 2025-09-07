package com.mamm.mammapps.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentRowUI
import com.mamm.mammapps.ui.theme.Dimensions

@Composable
fun HomeGridBottom(
    content: List<ContentRowUI>,
    columnListState: LazyListState,
    onContentClicked: (Int, ContentEntityUI) -> Unit
) {
    ProvideLazyListPivotOffset(parentFraction = 0.249f) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = Dimensions.paddingLarge,
                    vertical = Dimensions.paddingMedium
                ),
            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingLarge),
            state = columnListState
        ) {
            itemsIndexed(
                items = content,
                key = { _, item -> item.categoryName }
            ) { index, contentRow ->

                Column(
                    verticalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium)
                ) {
                    if (contentRow.categoryName.isNotEmpty()) {
                        Text(
                            text = contentRow.categoryName,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    RowOfContent(
                        contentList = contentRow.items,
                        onContentClick = { content ->
                            onContentClicked(index, content)
                        }
                    )
                }
            }
        }
    }
}