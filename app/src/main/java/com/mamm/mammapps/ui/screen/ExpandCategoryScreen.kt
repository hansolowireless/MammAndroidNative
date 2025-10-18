package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.ui.component.common.ContentEntity
import com.mamm.mammapps.ui.mapper.toContentEntityUI
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.viewmodel.ExpandCategoryViewModel
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.ui.mapper.toContentEntityUIList

@Composable
fun ExpandCategoryScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpandCategoryViewModel = hiltViewModel(),
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
            LazyVerticalGrid(
                columns = GridCells.Adaptive(100.dp),
                modifier = modifier.fillMaxWidth().padding(horizontal = Dimensions.paddingXSmall),
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
                        onClick = { onContentClick(item) }
                    )
                }
            }
        }

        is UIState.Error -> {

        }
    }


}