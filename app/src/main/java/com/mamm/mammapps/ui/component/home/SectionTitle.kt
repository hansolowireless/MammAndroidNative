package com.mamm.mammapps.ui.component.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.SectionTitleColor

@Composable
fun SectionTitle(
    modifier: Modifier = Modifier,
    title: String
) {
    Column(modifier = modifier.padding(start = Dimensions.paddingSmall)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = SectionTitleColor.title
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 2.dp
        )
    }
}