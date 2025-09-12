package com.mamm.mammapps.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamm.mammapps.ui.component.common.ContentEntity
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
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

    ProvideLazyListPivotOffset (parentFraction = 0.01f) {
        LazyRow (
            modifier = modifier.fillMaxWidth().wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = Dimensions.paddingMediumLarge),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium)
        ) {
            itemsIndexed(
                contentList) { _, contentEntity ->

                ContentEntity(
                    contentEntityUI = contentEntity,
                    onClick = { onContentClick(contentEntity) },
                    onFocus = { onFocus(contentEntity) }
                )
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
            subtitle = "Acción",
            imageUrl = "https://picsum.photos/400/300?random=1",
            identifier = ContentIdentifier.VoD("1")
        ),
        ContentEntityUI(
            title = "Película 2",
            subtitle = "Drama",
            imageUrl = "https://picsum.photos/400/300?random=2",
            identifier = ContentIdentifier.VoD("1")
        ),
        ContentEntityUI(
            title = "Película 3",
            subtitle = "Comedia",
            imageUrl = "https://picsum.photos/400/300?random=3",
            identifier = ContentIdentifier.VoD("1")
        ),
        ContentEntityUI(
            title = "Película 4",
            subtitle = "Terror",
            imageUrl = "https://picsum.photos/400/300?random=4",
            identifier = ContentIdentifier.VoD("1")
        ),
        ContentEntityUI(
            title = "Película 5",
            subtitle = "Ciencia Ficción",
            imageUrl = "https://picsum.photos/400/300?random=5",
            identifier = ContentIdentifier.VoD("1")
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

