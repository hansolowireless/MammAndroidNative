package com.mamm.mammapps.ui.screen

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.ui.component.HomeGridBottom
import com.mamm.mammapps.ui.component.home.HomeGridTop
import com.mamm.mammapps.ui.component.common.LoadingSpinner
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.component.dialog.PinDialog
import com.mamm.mammapps.ui.component.home.FeaturedCarousel
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.uistate.HomeContentUIState
import com.mamm.mammapps.ui.theme.Ratios
import com.mamm.mammapps.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    routeTag: AppRoute = AppRoute.HOME,
    onShowDetails: (item: ContentEntityUI) -> Unit,
    onPlay: (item: Any) -> Unit
) {
    val homeContentState = viewModel.homeContentUIState
    val homeContent = viewModel.homeContentUI
    val mobileFeatured by viewModel.mobileFeatured.collectAsStateWithLifecycle()
    val clickedContent by viewModel.clickedContent.collectAsStateWithLifecycle()
    val hasNavigated = remember { mutableStateOf(false) }

    val columnListState = rememberLazyListState()
    var lastClickedItemIndex by remember { mutableStateOf<Int?>(null) }
    val focusedContent by viewModel.focusedContent.collectAsStateWithLifecycle()

    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    LaunchedEffect(homeContentState) {
        when (homeContentState) {
            is HomeContentUIState.RequestContent -> viewModel.content(routeTag = routeTag)
            is HomeContentUIState.IncorrectPin -> backDispatcher?.onBackPressed()
            else -> {}
        }
    }

    LaunchedEffect(homeContent) {
        viewModel.setFirstFocusedContent()
    }

    LaunchedEffect(Unit) {
        viewModel.checkRestrictedScreen(routeTag)
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
                onPlay(content)
                hasNavigated.value = true
                viewModel.clearClickedContent()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (homeContentState) {
            is HomeContentUIState.Loading -> {
                LoadingSpinner()
            }

            is HomeContentUIState.Error -> {
                Text(
                    text = homeContentState.message,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is HomeContentUIState.Restricted -> {
                PinDialog(
                    onConfirm = {
                        viewModel.validatePin(pin = it)
                    },
                    onDismissRequest = {
                        backDispatcher?.onBackPressed()
                    }
                )
            }

            is HomeContentUIState.Success -> {
                Column {
                    if (LocalIsTV.current) {
                        focusedContent?.let {
                            HomeGridTop(
                                event = it
                            )
                        }
                    }

                    HomeGridBottom(
                        content = homeContent,
                        columnListState = columnListState,
                        mobileFeatured = if (!LocalIsTV.current) homeContent.find { it.isFeatured }?.items else null,
                        onContentClicked = { index, entityUI ->
                            lastClickedItemIndex = index

                            if (entityUI.identifier is ContentIdentifier.Channel) {
                                viewModel.findContent(
                                    entityUI = entityUI,
                                    routeTag = routeTag
                                )
                            } else {
                                onShowDetails(entityUI)
                            }

                        },
                        onFocus = { content ->
                            viewModel.setFocusedContent(content)
                        }
                    )
                }
            }

            is HomeContentUIState.Idle -> {
                //TODO Initial state
            }

            else -> {}
        }
    }
}

