package com.mamm.mammapps.ui.screen

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.domain.model.exception.GetHomeContentException
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.component.common.LoadingSpinner
import com.mamm.mammapps.ui.component.dialog.PinDialog
import com.mamm.mammapps.ui.component.home.HomeGridBottom
import com.mamm.mammapps.ui.component.home.HomeGridTop
import com.mamm.mammapps.ui.component.home.OperatorLogoBottomRight
import com.mamm.mammapps.ui.mapper.toContentToPlayUI
import com.mamm.mammapps.ui.mapper.toResId
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.uistate.CastState
import com.mamm.mammapps.ui.model.uistate.HomeContentUIState
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.viewmodel.CastViewModel
import com.mamm.mammapps.ui.viewmodel.HomeViewModel


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    castViewModel: CastViewModel = hiltViewModel(),
    routeTag: AppRoute = AppRoute.HOME,
    onShowDetails: (item: ContentEntityUI) -> Unit,
    onPlay: (item: Any) -> Unit,
    onExpandCategory: (categoryId: Int, categoryName: String) -> Unit = { _, _ -> },
    onErrorLogout: () -> Unit = {}
) {

    val isTV = LocalIsTV.current
    val castState by castViewModel.castState.collectAsStateWithLifecycle()

    val homeContentState = viewModel.homeContentUIState
    val homeContent by viewModel.homeContentUI.collectAsStateWithLifecycle()
    val operatorLogo by viewModel.operatorLogo.collectAsStateWithLifecycle()
    val clickedContent by viewModel.clickedContent.collectAsStateWithLifecycle()
    val hasNavigated = remember { mutableStateOf(false) }

    val focusedContent by viewModel.focusedContent.collectAsStateWithLifecycle()

    val lastClickedItemIndex by viewModel.lastClickedItemIndex.collectAsStateWithLifecycle()
    val columnListState = rememberLazyListState()
    val rememberedRowState by viewModel.rememberedRowState

    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val shouldResetFocus = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (!isTV) {
            castViewModel.startChromecast()
        }
    }

    LaunchedEffect(Unit) {
        shouldResetFocus.value = true
        viewModel.checkRestrictedScreen(routeTag)
    }

    LaunchedEffect(homeContentState) {
        when (homeContentState) {
            is HomeContentUIState.RequestContent -> viewModel.content(routeTag = routeTag)
            is HomeContentUIState.IncorrectPin -> backDispatcher?.onBackPressed()
            is HomeContentUIState.Success -> {
                //Para resetear el foco a 0 cada vez que se compone la pantalla
                val targetIndex = lastClickedItemIndex ?: 0
                if (columnListState.firstVisibleItemIndex != targetIndex) {
                    columnListState.scrollToItem(targetIndex)
                }
            }

            else -> {}
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (shouldResetFocus.value) {
                viewModel.setLastClickedIndexToZero()
            }
        }
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
                        shouldResetFocus.value = false
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
                logoUrl = operatorLogo
            )
        }

        is HomeContentUIState.PinRestriction -> {
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
            Box {
                if (LocalIsTV.current) {
                    focusedContent?.let {
                        HomeGridTop(
                            modifier = Modifier.height(430.dp),
                            content = it
                        )
                    }
                }
                Column {
                    if (LocalIsTV.current) {
                        Spacer(modifier = Modifier.height(260.dp))
                    }

                    HomeGridBottom(
                        columnListState = columnListState,
                        rememberedRowState = rememberedRowState,
                        focusedRowIndex = lastClickedItemIndex,
                        content = if (!LocalIsTV.current) homeContent.filter { !it.isFeatured } else homeContent,
                        mobileFeatured = if (!LocalIsTV.current) homeContent.find { it.isFeatured }?.items else null,
                        onContentClicked = { entityUI, rowState ->
                            viewModel.setLastClickedIndex(columnListState.firstVisibleItemIndex)
                            rowState?.let { viewModel.rememberRowState(it) }

                            if (entityUI.identifier is ContentIdentifier.Channel) {
                                viewModel.findContent(
                                    entityUI = entityUI,
                                    routeTag = routeTag
                                )
                            } else {
                                shouldResetFocus.value = false
                                onShowDetails(entityUI)
                            }
                        },
                        onFocus = { content ->
                            viewModel.setFocusedContent(content)
                        },
                        onExpandCategory = { categoryId, categoryName, rowState ->
                            viewModel.setLastClickedIndex(columnListState.firstVisibleItemIndex)
                            rowState?.let { viewModel.rememberRowState(it) }
                            shouldResetFocus.value = false
                            onExpandCategory(
                                categoryId,
                                categoryName
                            )
                        },
                        onRequestedFocus = {
                            /*Si no se hace esto, al darle a un canal se recompone la vista y llama a reset()
                            * En el caso de canales solo se limpiará al volver a la vista y enfocarse, cuando ya clickedContent será null
                            */
                            if (clickedContent == null) {
                                viewModel.setLastClickedIndexToNull()
                            }
                        }
                    )
                }
            }

            if (LocalIsTV.current) {
                OperatorLogoBottomRight(
                    logoUrl = operatorLogo
                )
            }
        }

        is HomeContentUIState.Error -> {
            when (homeContentState.throwable) {
                is GetHomeContentException.ForbiddenException -> {
                    onErrorLogout()
                }

                else -> {
                    Box(modifier = Modifier
                        .padding(horizontal = Dimensions.paddingLarge)
                        .fillMaxSize()) {
                        Text(
                            text = stringResource(homeContentState.throwable.toResId()),
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        is HomeContentUIState.Idle -> {
            //TODO Initial state
        }

        else -> {}
    }
}

