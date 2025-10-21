package com.mamm.mammapps.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.ui.component.common.ContentEntity
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.CustomizedContent
import com.mamm.mammapps.ui.theme.Dimensions

@Composable
fun RowOfContent(
    modifier: Modifier = Modifier,
    contentList: List<ContentEntityUI>,
    onContentClick: (ContentEntityUI) -> Unit,
    onFocus: (ContentEntityUI) -> Unit = {}
) {
    if (contentList.isEmpty()) {
        return
    }

    ProvideLazyListPivotOffset(parentFraction = 0.01f) {
        LazyRow(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = Dimensions.paddingXSmall),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
        ) {
            itemsIndexed(
                contentList
            ) { index, contentEntity ->

                ContentEntity(
                    contentEntityUI = contentEntity,
                    onClick = { onContentClick(contentEntity) },
                    onFocus = { onFocus(contentEntity) },
                    mostWatchedOrder = if (contentEntity.customContentType is CustomizedContent.MostWatchedType) (index + 1) else null
                )
            }
            item {
                if (LocalIsTV.current)
                    Spacer(modifier = Modifier.width(1000.dp))
            }
        }
    }

}


// Preview con datos de ejemplo
@Preview(showBackground = true)
@Composable
fun RowOfContentPreview() {
    val sampleContent = listOf(
        ContentEntityUI(
            title = "Película 1",
            imageUrl = "https://picsum.photos/400/300?random=1",
            identifier = ContentIdentifier.VoD(1)
        ),
        ContentEntityUI(
            title = "Película 2",
            imageUrl = "https://picsum.photos/400/300?random=2",
            identifier = ContentIdentifier.VoD(1)
        ),
        ContentEntityUI(
            title = "Película 3",
            imageUrl = "https://picsum.photos/400/300?random=3",
            identifier = ContentIdentifier.VoD(1)
        ),
        ContentEntityUI(
            title = "Película 4",
            imageUrl = "https://picsum.photos/400/300?random=4",
            identifier = ContentIdentifier.VoD(1)
        ),
        ContentEntityUI(
            title = "Película 5",
            imageUrl = "https://picsum.photos/400/300?random=5",
            identifier = ContentIdentifier.VoD(1)
        )
    )

    CompositionLocalProvider {
        Column {
            RowOfContent(
                contentList = sampleContent,
                onContentClick = { contentEntity ->
                    println("Clicked on: ${contentEntity.title}")
                }
            )
        }
    }
}

