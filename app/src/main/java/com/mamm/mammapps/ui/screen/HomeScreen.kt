package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
        modifier = Modifier
            .fillMaxSize()
    ) {
        when (homeContentState) {
            is UIState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is UIState.Error -> {
                Text(
                    text = homeContentState.message,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is UIState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = Dimensions.paddingMedium),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.paddingLarge)
                ) {
                    items(
                        count = homeContent.size,
                        key = { index -> homeContent[index].categoryName }
                    ) { index ->
                        val contentRow = homeContent[index]
                        Column {
                            // Título de la categoría
                            if (contentRow.categoryName.isNotEmpty()) {
                                Text(
                                    text = contentRow.categoryName,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(
                                        horizontal = Dimensions.paddingMedium,
                                        vertical = Dimensions.paddingSmall
                                    )
                                )
                            }

                            // Fila de contenido
                            RowOfContent(
                                contentList = contentRow.items,
                                onContentEntityClick = { contentEntity, index ->
                                    // Manejar click en item
                                    // Aquí puedes navegar a pantalla de detalle
                                }
                            )
                        }
                    }
                }
            }
            is UIState.Idle -> {
                // Estado inicial, no mostrar nada o un placeholder
            }
        }
    }
}