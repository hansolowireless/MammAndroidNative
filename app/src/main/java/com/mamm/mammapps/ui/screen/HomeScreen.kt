package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mamm.mammapps.ui.common.UIState
import com.mamm.mammapps.ui.component.RowOfContent
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val homeContentState = viewModel.homeContentState
    val homeContent = viewModel.homeContent

    LaunchedEffect(Unit) {
        viewModel.getHomeContent()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (homeContentState) {
            is UIState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is UIState.Error -> {
                Text(
                    text = (homeContentState as UIState.Error).message,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is UIState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(
                        horizontal = Dimensions.paddingLarge,
                        vertical = Dimensions.paddingMedium
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.paddingLarge)
                ) {
                    itemsIndexed(
                        items = homeContent,
                        key = { _, item -> item.categoryName }
                    ) { index, contentRow ->

                        // Get the LazyListState from the ViewModel.
                        // This state is persistent because it's stored in the ViewModel.
                        val rowListState = viewModel.getOrCreateScrollState(index)

                        Column(
                            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium)
                        ) {
                            // Title...
                            if (contentRow.categoryName.isNotEmpty()) {
                                Text(
                                    text = contentRow.categoryName,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            // Fila de contenido
                            RowOfContent(
                                contentList = contentRow.items,
                                lazyListState = rowListState, // Pass the persistent state
                                onContentEntityClick = { contentEntity, itemIndex ->
                                    // Handle click
                                }
                            )
                        }
                    }
                }

            }
            is UIState.Idle -> {
                // Initial state
            }
        }
    }
}
