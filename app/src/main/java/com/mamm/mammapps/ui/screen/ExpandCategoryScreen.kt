package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.domain.model.BrandedContent
import com.mamm.mammapps.domain.model.OtherContent
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.component.common.contententity.ContentEntity
import com.mamm.mammapps.ui.mapper.findContent
import com.mamm.mammapps.ui.mapper.toContentEntityUIList
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.ExpandCategoryColor
import com.mamm.mammapps.ui.viewmodel.ExpandCategoryViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ExpandCategoryScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpandCategoryViewModel = hiltViewModel(),
    categoryName: String,
    categoryId: Int? = null,
    appRoute: AppRoute? = null,
    onContentClick: (Any) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Fuera del when para que el scroll sobreviva a la navegación a Detail
    val gridState = rememberLazyGridState()
    val lastClickedFocusRequester = remember { FocusRequester() }
    val lastClickedIndex = viewModel.lastClickedIndex

    LaunchedEffect(Unit) {
        viewModel.getContent(
            categoryId = categoryId,
            route = appRoute
        )
    }

    when (val state = uiState) {
        is UIState.Loading -> {}

        is UIState.Success -> {
            LaunchedEffect(Unit) {
                if (lastClickedIndex != null) {
                    delay(50.milliseconds)
                    runCatching { lastClickedFocusRequester.requestFocus() }
                }
            }

            Column(modifier = modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = Dimensions.paddingMedium)) {
                    Text(
                        text = appRoute?.let {stringResource(it.getResId()) + " ▶ "} + categoryName,
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

                ProvideLazyListPivotOffset(parentFraction = 0.03f) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(100.dp),
                        modifier = Modifier.fillMaxWidth(),
                        state = gridState,
                        contentPadding = PaddingValues(
                            horizontal = Dimensions.paddingMedium,
                            vertical = Dimensions.paddingXLarge
                        ),
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall),
                        verticalArrangement = Arrangement.spacedBy(30.dp)
                    ) {
                        val responseData = state.data
                        val data = when (responseData) {
                            is OtherContent -> responseData.toContentEntityUIList()
                            is BrandedContent -> responseData.toContentEntityUIList()
                            else -> emptyList()
                        }

                        itemsIndexed(items = data) { index, item ->
                            ContentEntity(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (index == lastClickedIndex) Modifier.focusRequester(lastClickedFocusRequester)
                                        else Modifier
                                    ),
                                contentEntityUI = item,
                                onClick = {
                                    viewModel.setLastClickedIndex(index)
                                    when (responseData) {
                                        is OtherContent -> {
                                            responseData.findContent(item.identifier)?.let {
                                                onContentClick(it)
                                            }
                                        }

                                        is BrandedContent -> {
                                            responseData.findContent(item.identifier)?.let {
                                                onContentClick(it)
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(1000.dp))
                        }

                    }
                }

            }
        }

        is UIState.Error -> {

        }
    }


}