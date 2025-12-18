package com.mamm.mammapps.ui.component.common

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mamm.mammapps.R

@Composable
fun OperatorLogoImage(
    modifier: Modifier = Modifier,
    logoUrl: String?
) {
    AsyncImage(
        model = logoUrl,
        contentDescription = null,
        fallback = painterResource(R.drawable.logo_branding),
        modifier = modifier
    )
}