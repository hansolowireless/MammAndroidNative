package com.mamm.mammapps.ui.screen

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.component.detail.DetailMobile
import com.mamm.mammapps.ui.component.detail.DetailTV
import com.mamm.mammapps.ui.mapper.toContentToPlayUI
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.uistate.CastState
import com.mamm.mammapps.ui.viewmodel.CastViewModel
import com.mamm.mammapps.ui.viewmodel.DetailViewModel


@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    castViewModel: CastViewModel = hiltViewModel(),
    content: ContentEntityUI,
    prefoundContent: Any? = null,
    routeTag: AppRoute,
    onClickPlay: (Any) -> Unit,
    onSimilarContentClick: (Any) -> Unit
) {

    val isTV = LocalIsTV.current
    val castState by castViewModel.castState.collectAsStateWithLifecycle()

    val showPlayButton by viewModel.showPlayButton.collectAsStateWithLifecycle()
    val seasonInfoUIState by viewModel.seasonInfoUIState.collectAsStateWithLifecycle()
    val clickedContent by viewModel.clickedContent.collectAsStateWithLifecycle()
    val similarContent by viewModel.similarContent.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    LaunchedEffect(Unit) {
        viewModel.setRouteTag(routeTag)
        viewModel.setShowPlayButton(content)
    }

    LaunchedEffect(Unit) {
        if (!isTV) {
            castViewModel.startChromecast()
        }
    }

    LaunchedEffect(clickedContent) {
        clickedContent?.let {
            when (castState) {
                is CastState.SessionStarted -> {
                    castViewModel.loadRemoteMedia(it.toContentToPlayUI())
                }
                else -> {
                    onClickPlay(it)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        scrollState.scrollTo(0)
    }

    LaunchedEffect(Unit) {
        viewModel.getSeasonInfo(content)
    }

    LaunchedEffect(content) {
        viewModel.getSimilar(content.detailInfo?.subgenreId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearClickedContent()
        }
    }

    if (LocalIsTV.current) {
        DetailTV(
            content = content,
            showPlayButton = showPlayButton,
            similarContent = similarContent,
            seasonInfoUIState = seasonInfoUIState,
            onClickPlay = {
                viewModel.findContent(
                    entityUI = content,
                    prefoundContent = prefoundContent
                )
            },
            onClickEpisode = { seasonOrder, episodeId ->
                viewModel.findEpisode(seasonOrder, episodeId)
            },
            onSimilarContentClick = {
                onSimilarContentClick(it)
            }
        )
        return
    } else {
        DetailMobile(
            content = content,
            showPlayButton = showPlayButton,
            similarContent = similarContent,
            onClickPlay = {
                viewModel.findContent(
                    entityUI = content,
                    prefoundContent = prefoundContent
                )
            },
            seasonInfoUIState = seasonInfoUIState,
            onClickEpisode = { seasonOrder, episodeId ->
                viewModel.findEpisode(seasonOrder, episodeId)
            },
            onSimilarContentClick = {
                onSimilarContentClick(it)
            },
            onClose = {
                backDispatcher?.onBackPressed()
            }
        )
    }

}

