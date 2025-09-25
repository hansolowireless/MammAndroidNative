package com.mamm.mammapps.ui.component.common

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import coil.compose.AsyncImage
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentListUI
import com.mamm.mammapps.ui.theme.ContentEntityColor
import com.mamm.mammapps.ui.theme.ContentEntityListItemColor

@Composable
fun ContentEntityListItem (
    content: ContentListUI,
    showLiveIndicator: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }

    ListItem(
        selected = false,
        onClick = {
            //TODO
        },
        colors = ListItemDefaults.colors(
            containerColor = ContentEntityListItemColor.unfocusedContent,
            focusedContainerColor = ContentEntityListItemColor.focusedContent,
        ),
        modifier = Modifier.onFocusChanged { isFocused = it.isFocused },
        headlineContent = {
            Text(
                text = content.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = if (isFocused) 2 else 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = if (isFocused) {
            content.detailInfo?.description?.let { description ->
                if (description.isNotBlank()) {
                    {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else null
            }
        } else null,
        trailingContent = content.imageUrl.let { imageUrl ->
            {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = content.detailInfo?.description,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    )
}