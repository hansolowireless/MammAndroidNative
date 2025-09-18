package com.mamm.mammapps.ui.component.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.Metadata
import com.mamm.mammapps.ui.theme.Dimensions

@Composable
fun DurationYearRatingRow(metadata: Metadata) {
    val items = buildList {
        if (metadata.durationMin.isNotBlank()) {
            add(stringResource(R.string.duration_minutes, metadata.durationMin))
        }
        if (metadata.country.isNotBlank()) {
            add(metadata.country)
        }
    }

    if (items.isNotEmpty() || metadata.ratingURL != null) {
        Row(
            modifier = Modifier.padding(bottom = Dimensions.paddingXSmall),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingXSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                Text(text = item, color = Color.White)
                if (index < items.size - 1) {
                    Text(text = stringResource(R.string.separator), color = Color.White)
                }
            }

            metadata.ratingURL?.let { ratingUrl ->
                AsyncImage(
                    model = ratingUrl,
                    contentDescription = stringResource(R.string.rating_content_description),
                    modifier = Modifier.size(Dimensions.paddingLarge)
                )
            }
        }
    }
}