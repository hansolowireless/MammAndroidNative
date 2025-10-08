package com.mamm.mammapps.ui.component.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.common.deviceAdaptivePadding

@Composable
fun LoginTV(
    modifier: Modifier = Modifier,
    onLogin: (String, String) -> Unit
) {
    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(0.4f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_masmedia),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .fillMaxHeight(0.5f),
                contentScale = ContentScale.Fit
            )
        }

        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight(0.6f)
                .width(1.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .padding(deviceAdaptivePadding()),
            contentAlignment = Alignment.Center
        ) {
            LoginForm(
                onLogin = { email, password ->
                    onLogin(email, password)
                }
            )
        }
    }
}