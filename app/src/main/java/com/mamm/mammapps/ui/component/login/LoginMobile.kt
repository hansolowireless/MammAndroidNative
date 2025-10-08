package com.mamm.mammapps.ui.component.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.TextPrimary

@Composable
fun LoginMobile(
    modifier: Modifier = Modifier,
    onLogin: (String, String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = Dimensions.paddingMediumLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.width(250.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_masmedia),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        LoginForm(
            onLogin = { email, password -> onLogin(email, password) },
            showInitialText = false
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(R.string.forgot_password),
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(80.dp))
    }
}