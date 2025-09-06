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

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onContentClicked: (item: Any) -> Unit
) {
    val homeContentState = viewModel.homeContentUIState
    val homeContent = viewModel.homeContentUI
    val clickedContent by viewModel.clickedContent.collectAsStateWithLifecycle()

    val columnListState = rememberLazyListState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getHomeContent()
    }

    LaunchedEffect(clickedContent) {
        clickedContent?.let {
            onContentClicked(it)
            viewModel.clearClickedContent()
        } ?: Toast.makeText(context, "Content not found", Toast.LENGTH_SHORT).show()
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
                        onContentClicked = { entityUI ->
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

