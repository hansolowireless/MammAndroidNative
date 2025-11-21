package com.mamm.mammapps.ui.component.home

import android.util.Log
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.ContentRowUI
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.HomeGridBottomColor

@Composable
fun HomeGridBottom(
    content: List<ContentRowUI>,
    columnListState: LazyListState,
    rememberedRowState: Pair<Int, Int>?,
    mobileFeatured: List<ContentEntityUI>? = null,
    onContentClicked: (ContentEntityUI, LazyListState?) -> Unit,
    onExpandCategory: (Int, String, LazyListState?) -> Unit,
    onFocus: (ContentEntityUI) -> Unit = {},
    focusedRowIndex: Int?,
    onRequestedFocus: () -> Unit = {}
) {

    val expandCategoryTitle = stringResource(id = R.string.expand_category_content_title)

    ProvideLazyListPivotOffset(parentFraction = 0.248f) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = Dimensions.paddingXSmall),
            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingLarge),
            state = columnListState
        ) {
            item {
                mobileFeatured?.let {
                    FeaturedCarousel(
                        modifier = Modifier.fillMaxWidth(),
                        content = it,
                        onItemClick = { entityUI ->
                            onContentClicked(entityUI, null)
                        }
                    )
                }
            }

            itemsIndexed(
                items = content,
                key = { index, item -> "${item.categoryId}_$index" }
            ) { index, contentRow ->

                /*Para restaurar el foco HORIZONTAL
                 Donde estaba antes de ir a Details o al Player
                */
                val shouldRestoreState = index == focusedRowIndex && rememberedRowState != null

                val rowState = rememberLazyListState(
                    initialFirstVisibleItemIndex = if (shouldRestoreState) rememberedRowState!!.first else 0,
                    initialFirstVisibleItemScrollOffset = if (shouldRestoreState) rememberedRowState!!.second else 0
                )

                /*Para restaurar el foco VERTICAL
                 Donde estaba antes de ir a Details o al Player
                */
                val rowFocusRequester = remember(contentRow.categoryId) { FocusRequester() }

                LaunchedEffect(focusedRowIndex) {
                    if (index == focusedRowIndex) {
                        Log.d("HomeGrid", "Focus on row $index")
                        Log.d("HomeGrid", "focusedRowIndex $focusedRowIndex")
                        Log.d("HomeGrid", "La key es ${contentRow.categoryId}")
                        kotlinx.coroutines.delay(50)
                        rowFocusRequester.requestFocus()

                        //Hacerlo null para que no vuelva a enfocar
                        onRequestedFocus()
                    }
                }

                Column(
                    modifier = Modifier.padding(
                        horizontal = Dimensions.paddingLarge,
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                                    contentRow.categoryName,
                                    rowState
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
                        lazyListState =  rowState,
                        showExpandCategory = LocalIsTV.current && contentRow.loadMore,
                        onContentClick = { content ->
                            onContentClicked(content, rowState)
                        },
                        onFocus = { content ->
                            onFocus(content)
                        },
                        onExpandCategoryClick = {
                            onExpandCategory(
                                contentRow.categoryId,
                                contentRow.categoryName,
                                rowState
                            )
                        },
                        onFocusExpandCategory = {
                            onFocus(
                                ContentEntityUI(
                                    identifier = ContentIdentifier.VoD(0),
                                    title = expandCategoryTitle
                                )
                            )
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