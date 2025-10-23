package com.mamm.mammapps.ui.component.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.component.RowOfContent
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentRowUI
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.HomeGridBottomColor

@Composable
fun HomeGridBottom(
    content: List<ContentRowUI>,
    columnListState: LazyListState,
    mobileFeatured: List<ContentEntityUI>? = null,
    onContentClicked: (Int, ContentEntityUI) -> Unit,
    onExpandCategory: (Int, String) -> Unit,
    onFocus: (ContentEntityUI) -> Unit = {},
    focusedRowIndex: Int?
) {

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        // pequeño delay para que Compose reactive el árbol tras popBackStack()
        kotlinx.coroutines.delay(100)
        focusRequester.requestFocus()
    }

    ProvideLazyListPivotOffset(parentFraction = 0.249f) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .padding(top = Dimensions.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingLarge),
            state = columnListState
        ) {
            item {
                mobileFeatured?.let {
                    FeaturedCarousel(
                        modifier = Modifier.fillMaxWidth(),
                        content = it,
                        onItemClick = { entityUI ->
                            onContentClicked(0, entityUI)
                        }
                    )
                }
            }

            itemsIndexed(
                items = content,
                key = { index, item -> "${item.categoryName}_$index" }
            ) { index, contentRow ->

                // 1. Creamos un FocusRequester para esta fila.
                val rowFocusRequester = remember { FocusRequester() }

                // 2. Si el índice de esta fila coincide con el que queremos enfocar,
                //    lanzamos un efecto para solicitar el foco.
                LaunchedEffect(Unit) {
                    if (index == focusedRowIndex) {
                        // Un pequeño delay es útil para asegurar que todo esté compuesto
                        // antes de pedir el foco.
                        kotlinx.coroutines.delay(100)
                        rowFocusRequester.requestFocus()
                    }
                }

                Column(
                    modifier = Modifier.padding(
                        horizontal = Dimensions.paddingLarge,
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium)
                ) {
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = contentRow.categoryName,
                            color = HomeGridBottomColor.rowTitle,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        if (contentRow.loadMore && !LocalIsTV.current) {
                            IconButton(onClick = {
                                onExpandCategory(
                                    contentRow.categoryId,
                                    contentRow.categoryName
                                )
                            }) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = stringResource(R.string.accessibility_expandcategory),
                                    tint = HomeGridBottomColor.rowTitle
                                )
                            }
                        }
                    }

                    RowOfContent(
                        modifier = Modifier.focusRequester(rowFocusRequester),
                        contentList = contentRow.items,
                        onContentClick = { content ->
                            onContentClicked(index, content)
                        },
                        onFocus = { content ->
                            onFocus(content)
                        }
                    )

                }
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}