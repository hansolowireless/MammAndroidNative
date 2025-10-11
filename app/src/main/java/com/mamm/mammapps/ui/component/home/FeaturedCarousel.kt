package com.mamm.mammapps.ui.component.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.ui.component.common.ContentEntity
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.Ratios
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeaturedCarousel(
    modifier: Modifier = Modifier,
    content: List<ContentEntityUI>,
    onItemClick: (ContentEntityUI) -> Unit = {}
) {
    if (content.isEmpty()) return

    val pageCount = Int.MAX_VALUE
    val pagerState = rememberPagerState(
        initialPage = pageCount / 2,
        pageCount = { pageCount }
    )

    LaunchedEffect(pagerState) {
        while (true) {
            delay(5000)
            val nextPage = pagerState.currentPage + 1
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = modifier.fillMaxWidth().aspectRatio(Ratios.HORIZONTAL),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            contentPadding = PaddingValues(bottom = 0.dp),
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            pageSpacing = 0.dp
        ) { page ->
            val itemIndex = page % content.size
            val item = remember(itemIndex) { content[itemIndex] }

            ContentEntity(
                modifier = Modifier.fillMaxWidth(),
                contentEntityUI = item,
                onClick = { onItemClick(item) }
            )
        }

        Row(
            modifier = Modifier.fillMaxSize().align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            repeat(content.size) { index ->
                CarouselIndicatorDot(isSelected = index == pagerState.currentPage % content.size)
            }
        }
    }
}

@Composable
private fun CarouselIndicatorDot(isSelected: Boolean) {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .size(8.dp)
            .clip(CircleShape)
            .background(
                color = if (isSelected) Color.White else Color.Gray.copy(alpha = 0.5f)
            )
    )
}
