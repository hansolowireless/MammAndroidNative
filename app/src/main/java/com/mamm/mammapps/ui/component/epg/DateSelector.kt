package com.mamm.mammapps.ui.component.epg

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.mapper.toDateSelectorResId
import java.time.LocalDate

@Composable
fun DateSelector(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val dates = remember {
        listOf(
            LocalDate.now().minusDays(7),
            LocalDate.now().minusDays(6),
            LocalDate.now().minusDays(5),
            LocalDate.now().minusDays(4),
            LocalDate.now().minusDays(3),
            LocalDate.now().minusDays(2),
            LocalDate.now().minusDays(1),
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(2)
        )
    }

    if (LocalIsTV.current) {
        TvDateSelector(
            modifier = modifier,
            dates = dates,
            selectedDate = selectedDate,
            onDateSelected = onDateSelected
        )
    } else {
        MobileDateSelector(
            modifier = modifier,
            dates = dates,
            selectedDate = selectedDate,
            onDateSelected = onDateSelected
        )
    }
}

@Composable
private fun MobileDateSelector(
    modifier: Modifier = Modifier,
    dates: List<LocalDate>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val selectedTabIndex = dates.indexOf(selectedDate)
    val todayTabIndex = remember(dates) { dates.indexOf(LocalDate.now()) }

    val scrollState = rememberScrollState()
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp

    val density = LocalDensity.current

    ScrollableTabRow(
        modifier = modifier,
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        edgePadding = 0.dp,
        indicator = { tabPositions ->
            if (selectedTabIndex in tabPositions.indices) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .width(4.dp)
                        .height(4.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LaunchedEffect(tabPositions, todayTabIndex) {
                if (todayTabIndex != -1 && todayTabIndex < tabPositions.size) {
                    val currentTabPosition = tabPositions[todayTabIndex]
                    val centeredScroll = (currentTabPosition.left + currentTabPosition.width / 2 - screenWidthDp / 2)

                    val scrollOffsetPx = with(density) { centeredScroll.roundToPx() }
                    scrollState.animateScrollTo(scrollOffsetPx)
                }
            }
        },
        divider = {}
    ) {
        dates.forEachIndexed { index, date ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onDateSelected(date) },
                text = {
                    Text(
                        text = date.toDateSelectorResId()?.let {
                            stringResource(id = it)
                        } ?: date.toString(),
                        color = if (selectedTabIndex == index) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun TvDateSelector(
    modifier: Modifier = Modifier,
    dates: List<LocalDate>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        dates.forEach { date ->
            ListItem(
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedContainerColor = Color.Transparent
                ),
                headlineContent = {
                    Text(
                        text = date.toDateSelectorResId()?.let {
                            stringResource(id = it)
                        } ?: date.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center
                    )
                },
                trailingContent = {
                    if (date == selectedDate) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                onClick = {
                    onDateSelected(date)
                },
                selected = date == selectedDate
            )
        }
    }
}
