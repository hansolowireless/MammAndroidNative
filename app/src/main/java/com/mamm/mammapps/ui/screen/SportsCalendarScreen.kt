package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowDefaults
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.component.common.ContentEntityListItem
import com.mamm.mammapps.ui.component.common.LoadingSpinner
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.viewmodel.SportsCalendarViewModel
import kotlinx.coroutines.delay

@Composable
fun SportsCalendarScreen(
    viewModel: SportsCalendarViewModel = hiltViewModel(),
    onShowDetails: () -> Unit = {},
    onPlayChannel: (Channel) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val competitions by viewModel.competitions.collectAsStateWithLifecycle()
    val selectedCompetition by viewModel.selectedCompetition.collectAsStateWithLifecycle()
    val eventsGroupedByDay by viewModel.eventsGroupedByDay.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadEvents()
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { channel ->
            onPlayChannel(channel)
        }
    }

    when (val state = uiState) {
        is UIState.Idle -> {}
        is UIState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingSpinner()
            }
        }
        is UIState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message, color = Color.Red)
            }
        }
        is UIState.Success -> {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

                // TAB NAVIGATION ROW
                ProvideLazyListPivotOffset(parentFraction = .02f) {
                    val allLabel = stringResource(id = R.string.all)
                    val allTabs = remember(competitions, allLabel) {
                        listOf(null) + competitions
                    }

                    if (LocalIsTV.current) {
                        if (allTabs.isNotEmpty()) {
                            val tabFocusRequesters = remember(allTabs) {
                                allTabs.map { FocusRequester() }
                            }
                            
                            var selectedTabIndex by remember(selectedCompetition, allTabs) {
                                val index = allTabs.indexOf(selectedCompetition)
                                mutableIntStateOf(if (index != -1) index else 0)
                            }
    
                            TabRow(
                                selectedTabIndex = selectedTabIndex,
                                indicator = { tabPositions, doesTabRowHaveFocus ->
                                    TabRowDefaults.PillIndicator(
                                        currentTabPosition = tabPositions[selectedTabIndex],
                                        activeColor = Color.White.copy(alpha = 0.4f),
                                        doesTabRowHaveFocus = doesTabRowHaveFocus
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                                    .padding(horizontal = Dimensions.paddingMedium)
                                    .focusRestorer(tabFocusRequesters[selectedTabIndex])
                                    .focusGroup()
                            ) {
                                allTabs.forEachIndexed { index, tab ->
                                    val isSelected = (tab == selectedCompetition)
    
                                    Tab(
                                        selected = isSelected,
                                        onFocus = {
                                            selectedTabIndex = index
                                            viewModel.selectTab(tab)
                                        },
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp, vertical = 6.dp)
                                            .focusRequester(tabFocusRequesters[index]),
                                        colors = TabDefaults.pillIndicatorTabColors(
                                            contentColor = Color.White,
                                            focusedContentColor = Color.White,
                                            focusedSelectedContentColor = Color.White,
                                            inactiveContentColor = Color.White.copy(alpha = 1f),
                                            disabledContentColor = Color.White.copy(alpha = 1f),
                                            disabledInactiveContentColor = Color.White,
                                        )
                                    ) {
                                        androidx.tv.material3.Text(
                                            text = tab ?: allLabel,
                                            style = androidx.tv.material3.MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(1000.dp))
                            }
                        }
                    } else {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .padding(horizontal = Dimensions.paddingMedium),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(allTabs) { tab ->
                                val isSelected = tab == selectedCompetition

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) Color.White else Color.DarkGray)
                                        .clickable { viewModel.selectTab(tab) }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = tab ?: allLabel,
                                        color = if (isSelected) Color.Black else Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // EVENTS LIST
                ProvideLazyListPivotOffset(parentFraction = .1f) {
                    val listState = rememberLazyListState()
                    val firstItemFocusRequester = remember { FocusRequester() }
                    var focusRequested by remember { mutableStateOf(false) }
                    val firstEvent = remember(eventsGroupedByDay) {
                        eventsGroupedByDay.values.firstOrNull()?.firstOrNull()
                    }
                    val isTv = LocalIsTV.current

                    LaunchedEffect(eventsGroupedByDay) {
                        if (eventsGroupedByDay.isNotEmpty() && !focusRequested && isTv) {
                            try {
                                delay(100) // Small delay to let items compose
                                firstItemFocusRequester.requestFocus()
                                focusRequested = true
                            } catch (e: Exception) {
                                // FocusRequester not ready
                            }
                        }
                    }

                    LaunchedEffect(selectedCompetition) {
                        listState.scrollToItem(0)
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Dimensions.paddingMedium),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        eventsGroupedByDay.forEach { (day, events) ->
                            stickyHeader(key = day) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black)
                                        .padding(bottom = 12.dp, top = 16.dp)
                                ) {
                                    Text(
                                        text = day,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            items(events) { event ->
                                val focusModifier = if (event == firstEvent) {
                                    Modifier.focusRequester(firstItemFocusRequester)
                                } else {
                                    Modifier
                                }

                                ContentEntityListItem(
                                    modifier = focusModifier,
                                    mainContent = event,
                                    showDescription = true,
                                    showLiveIndicator = event.isLive,
                                    onClick = {
                                        if (event.isLive) {
                                            viewModel.onEventClicked(event)
                                        } else {
                                            onShowDetails()
                                        }
                                    }
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(500.dp))
                        }
                    }
                }
            }
        }
    }
}
