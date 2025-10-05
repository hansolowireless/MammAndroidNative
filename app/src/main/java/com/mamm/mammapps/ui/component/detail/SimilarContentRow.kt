package com.mamm.mammapps.ui.component.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.mamm.mammapps.ui.component.RowOfContent
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentRowUI
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.HomeGridBottomColor

@Composable
fun SimilarContentRow(
    modifier : Modifier = Modifier,
    content: ContentRowUI,
    onContentClicked: (ContentEntityUI) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium)
    ) {
        if (content.categoryName.isNotEmpty()) {
            Text(
                text = content.categoryName,
                color = HomeGridBottomColor.rowTitle,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        RowOfContent(
            contentList = content.items,
            onContentClick = { content ->
                onContentClicked(content)
            }
        )
    }
}