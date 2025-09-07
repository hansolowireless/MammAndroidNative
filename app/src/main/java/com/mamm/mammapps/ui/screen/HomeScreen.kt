package com.mamm.mammapps.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.ui.common.UIState
import com.mamm.mammapps.ui.component.HomeGridBottom
import com.mamm.mammapps.ui.component.HomeGridTop
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onContentClicked: (item: Any) -> Unit
) {
    val homeContentState = viewModel.homeContentUIState
    val homeContent = viewModel.homeContentUI
    val clickedContent by viewModel.clickedContent.collectAsStateWithLifecycle()
    val hasNavigated = remember { mutableStateOf(false) }

    val columnListState = rememberLazyListState()

    var lastClickedItemIndex by remember { mutableStateOf<Int?>(null) }


    LaunchedEffect(Unit) {
        viewModel.getHomeContent()
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
                Column {
                    HomeGridTop(
                        event = homeContent.first().items.first()
                    )

                    HomeGridBottom(
                        content = homeContent,
                        columnListState = columnListState,
                        onContentClicked = { index, entityUI ->
                            lastClickedItemIndex = index
                            viewModel.findContent(entityUI)
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

