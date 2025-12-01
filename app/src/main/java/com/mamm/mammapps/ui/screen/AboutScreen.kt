package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.common.OperatorLogoImage
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.TextPrimary
import com.mamm.mammapps.ui.viewmodel.AboutViewModel

@Composable
fun AboutScreen(
    viewModel: AboutViewModel = hiltViewModel()
) {
    val aboutInfo by viewModel.aboutInfo.collectAsStateWithLifecycle()
    val operatorLogo by viewModel.operatorLogo.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getAboutInfo()
        viewModel.getOperatorLogo()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.paddingLarge)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        OperatorLogoImage(
            logoUrl = operatorLogo
        )

        Spacer(modifier = Modifier.height(24.dp))

        aboutInfo?.let { info ->
            Text(
                text = "${stringResource(R.string.username)}: ${info.userName}",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${stringResource(R.string.application_version)}: ${info.appVersion}",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
        }
    }
}
