package com.mamm.mammapps.ui.component.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import kotlinx.coroutines.delay

@Composable
fun FeaturedCarousel(
    modifier: Modifier = Modifier,
    contents: List<ContentEntityUI>,
    onItemClick: (ContentEntityUI) -> Unit = {}
) {
    if (contents.isEmpty()) return

    val pageCount = Int.MAX_VALUE
    val pagerState = rememberPagerState(
        initialPage = pageCount / 2,
        pageCount = { pageCount }
    )

    LaunchedEffect(key1 = pagerState.currentPage) {
        delay(5000)
        pagerState.animateScrollToPage((pagerState.currentPage + 1) % pageCount)
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val itemIndex = page % contents.size
            val content = remember(itemIndex) { contents[itemIndex] }

            ContentEntity(
                modifier = Modifier.fillMaxSize(),
                contentEntityUI = content,
                onClick = { onItemClick(content) }
            )
        }

        Row(
            modifier = Modifier
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val selectedIndex = pagerState.currentPage % contents.size
            contents.forEachIndexed { index, _ ->
                CarouselIndicatorDot(isSelected = index == selectedIndex)
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
