package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.ui.component.common.ContentEntity
import com.mamm.mammapps.ui.mapper.findContent
import com.mamm.mammapps.ui.mapper.toContentEntityUIList
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.ExpandCategoryColor
import com.mamm.mammapps.ui.viewmodel.ExpandCategoryViewModel

@Composable
fun ExpandCategoryScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpandCategoryViewModel = hiltViewModel(),
    categoryName: String,
    categoryId: Int? = null,
    onContentClick: (Any) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getContent(categoryId = categoryId)
    }

    when (val state = uiState) {
        is UIState.Loading -> {

        }

        is UIState.Success -> {
            Column(modifier = modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = Dimensions.paddingMedium)) {
                    Text(
                        text = categoryName,
                        color = ExpandCategoryColor.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(
                            top = Dimensions.paddingSmall,
                            bottom = Dimensions.paddingSmall
                        )
                    )
                    HorizontalDivider(
                        color = ExpandCategoryColor.title.copy(alpha = 0.2f),
                        modifier = Modifier.padding(
                            bottom = Dimensions.paddingSmall
                        )
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(100.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        horizontal = Dimensions.paddingMedium,
                        vertical = Dimensions.paddingSmall
                    ),
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.paddingXSmall)
                ) {
                    items(items = state.data.toContentEntityUIList()) { item ->
                        ContentEntity(
                            contentEntityUI = item,
                            onClick = {
                                state.data.findContent(item.identifier)?.let {
                                    onContentClick(it)
                                }
                            }
                        )
                    }
                }
            }
        }

        is UIState.Error -> {

        }
    }


}