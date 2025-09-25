package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.ui.common.UIState
import com.mamm.mammapps.ui.component.HomeGridBottom
import com.mamm.mammapps.ui.component.HomeGridTop
import com.mamm.mammapps.ui.component.common.LoadingSpinner
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    routeTag: AppRoute = AppRoute.HOME,
    onContentClicked: (item: Any) -> Unit
) {
    val homeContentState = viewModel.homeContentUIState
    val homeContent = viewModel.homeContentUI
    val clickedContent by viewModel.clickedContent.collectAsStateWithLifecycle()
    val hasNavigated = remember { mutableStateOf(false) }

    val columnListState = rememberLazyListState()

    var lastClickedItemIndex by remember { mutableStateOf<Int?>(null) }

    val focusedContent by viewModel.focusedContent.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.content(routeTag)
    }

    LaunchedEffect(lastClickedItemIndex) {
        lastClickedItemIndex?.let { index ->
            if (index >= 0 && index < homeContent.size) {
                columnListState.scrollToItem(index)
            }
        }
    }

    LaunchedEffect(clickedContent) {
        clickedContent?.let { content ->
            if (!hasNavigated.value) {
                onContentClicked(content)
                hasNavigated.value = true
                viewModel.clearClickedContent()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (homeContentState) {
            is UIState.Loading -> {
                LoadingSpinner()
            }

            is UIState.Error -> {
                Text(
                    text = homeContentState.message,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is UIState.Success -> {
                Column {
                    focusedContent?.let {
                        HomeGridTop(
                            event = it
                        )
                    }
                    HomeGridBottom(
                        content = homeContent,
                        columnListState = columnListState,
                        onContentClicked = { index, entityUI ->
                            lastClickedItemIndex = index
                            viewModel.findContent(
                                entityUI = entityUI,
                                routeTag = routeTag
                            )
                        },
                        onFocus = { content ->
                            viewModel.setFocusedContent(content)
                        }
                    )
                }
            }

            is UIState.Idle -> {
                //TODO Initial state
            }
        }
    }
}

