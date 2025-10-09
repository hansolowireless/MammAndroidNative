package com.mamm.mammapps.ui.component.metadata

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.metadata.Metadata
import com.mamm.mammapps.ui.theme.DetailColor
import com.mamm.mammapps.ui.theme.Dimensions

@Composable
fun DirectorAndGenreRow(
    modifier: Modifier = Modifier,
    metadata: Metadata
) {
    // Director y género
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingXSmall)
    ) {
        if (metadata.director.isNotBlank())
            Text(
                text = stringResource(
                    R.string.director_label,
                    metadata.director
                ),
                color = DetailColor.metadata
            )
        if (metadata.director.isNotBlank() && metadata.genres.isNotBlank()) {
            Text(
                text = stringResource(R.string.separator),
                color = DetailColor.metadata
            )
        }
        if (metadata.genres.isNotBlank())
            Text(
                text = metadata.genres,
                color = DetailColor.metadata
            )
    }
}