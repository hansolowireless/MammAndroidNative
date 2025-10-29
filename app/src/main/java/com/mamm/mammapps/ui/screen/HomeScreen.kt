package com.mamm.mammapps.ui.screen

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.component.common.LoadingSpinner
import com.mamm.mammapps.ui.component.dialog.PinDialog
import com.mamm.mammapps.ui.component.home.HomeGridBottom
import com.mamm.mammapps.ui.component.home.HomeGridTop
import com.mamm.mammapps.ui.mapper.toContentToPlayUI
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.uistate.CastState
import com.mamm.mammapps.ui.model.uistate.HomeContentUIState
import com.mamm.mammapps.ui.viewmodel.CastViewModel
import com.mamm.mammapps.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    castViewModel: CastViewModel = hiltViewModel(),
    routeTag: AppRoute = AppRoute.HOME,
    onShowDetails: (item: ContentEntityUI) -> Unit,
    onPlay: (item: Any) -> Unit,
    onExpandCategory: (categoryId: Int, categoryName: String) -> Unit = { _, _ -> }
) {

    val isTV = LocalIsTV.current
    val castState by castViewModel.castState.collectAsStateWithLifecycle()

    val homeContentState = viewModel.homeContentUIState
    val homeContent by viewModel.homeContentUI.collectAsStateWithLifecycle()
    val clickedContent by viewModel.clickedContent.collectAsStateWithLifecycle()
    val hasNavigated = remember { mutableStateOf(false) }

    val lastClickedItemIndex by viewModel.lastClickedItemIndex.collectAsStateWithLifecycle()
    val columnListState =
        rememberLazyListState(initialFirstVisibleItemIndex = lastClickedItemIndex ?: 0)
    val focusedContent by viewModel.focusedContent.collectAsStateWithLifecycle()

    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    LaunchedEffect(Unit) {
        if (!isTV) {
            castViewModel.startChromecast()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkRestrictedScreen(routeTag)
    }

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

    LaunchedEffect(clickedContent) {
        clickedContent?.let {
            when (castState) {
                is CastState.SessionStarted -> {
                    castViewModel.loadRemoteMedia(it.toContentToPlayUI())
                }
                else -> {
                    if (!hasNavigated.value) {
                        onPlay(it)
                        hasNavigated.value = true
                    }
                }
            }
            viewModel.clearClickedContent()
        }
    }

    when (homeContentState) {
        is HomeContentUIState.Loading -> {
            LoadingSpinner(
                modifier = Modifier.fillMaxSize(),
                logoUrl = homeContentState.logo
            )
        }


        is HomeContentUIState.Error -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = homeContentState.message,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
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
                            content = it
                        )
                    }
                }

                HomeGridBottom(
                    columnListState = columnListState,
                    content = if (!LocalIsTV.current) homeContent.filter { !it.isFeatured } else homeContent,
                    mobileFeatured = if (!LocalIsTV.current) homeContent.find { it.isFeatured }?.items else null,
                    onContentClicked = { index, entityUI ->
                        viewModel.setLastClickedIndex(index)

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
                    },
                    focusedRowIndex = lastClickedItemIndex,
                    onExpandCategory = { categoryId, categoryName ->
                        onExpandCategory(
                            categoryId,
                            categoryName
                        )
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

