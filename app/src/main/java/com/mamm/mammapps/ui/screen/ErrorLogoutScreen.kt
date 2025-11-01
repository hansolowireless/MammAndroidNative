package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.mamm.mammapps.data.model.exception.GetHomeContentException
import com.mamm.mammapps.ui.component.common.OperatorLogoImage
import com.mamm.mammapps.ui.component.common.PrimaryButton
import com.mamm.mammapps.ui.mapper.toResId
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.TextPrimary
import com.mamm.mammapps.ui.viewmodel.LogoutViewModel

@Composable
fun ErrorLogoutScreen(
    viewModel: LogoutViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val operatorLogo by viewModel.operatorLogo.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getOperatorLogo()
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is UIState.Success<*> -> onNavigateToLogin()
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.paddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        operatorLogo?.let { url ->
            OperatorLogoImage(logoUrl = url)
            Spacer(modifier = Modifier.height(Dimensions.paddingXSmall))
        }

        Text(
            text = stringResource(GetHomeContentException.ForbiddenException.toResId()),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(Dimensions.paddingLarge))

        PrimaryButton(
            modifier = Modifier.width(200.dp),
            text = stringResource(id = com.mamm.mammapps.R.string.logout),
            onClick = {
                viewModel.logout()
            }
        )
    }


}