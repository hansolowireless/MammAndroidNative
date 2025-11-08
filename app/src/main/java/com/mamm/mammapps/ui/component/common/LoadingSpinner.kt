package com.mamm.mammapps.ui.component.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.Primary

@Composable
fun LoadingSpinner(
    modifier: Modifier = Modifier,
    logoUrl: String? = null
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            logoUrl?.let {
                OperatorLogoImage(
                    logoUrl = logoUrl,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 300.dp, height = 110.dp)
                )
            }
            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = Primary
            )
        }
    }
}