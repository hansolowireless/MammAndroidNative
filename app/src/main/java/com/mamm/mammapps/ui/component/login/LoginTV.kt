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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.common.deviceAdaptivePadding
import com.mamm.mammapps.ui.component.common.PrimaryButton
import com.mamm.mammapps.domain.model.loginwithcode.LoginCodeGenerate

@Composable
fun LoginTV(
    modifier: Modifier = Modifier,
    tvCodeData: LoginCodeGenerate? = null,
    onLogin: (String, String) -> Unit,
    onTvCodeTabSelected: () -> Unit,
    onCancelCodePoll: () -> Unit
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
                painter = painterResource(id = R.drawable.logo_branding),
                contentDescription = null,
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
            var selectedTabIndex by remember { mutableIntStateOf(0) }
            val tabs = listOf(
                stringResource(id = R.string.password),
                stringResource(id = R.string.tv_code)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                                if (index == 1) {
                                    onTvCodeTabSelected()
                                } else if (index == 0) {
                                    onCancelCodePoll()
                                }
                            },
                            text = {
                                Text(
                                    title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        )
                    }
                }

                if (selectedTabIndex == 0) {
                    LoginForm(
                        onLogin = { email, password ->
                            onLogin(email, password)
                        }
                    )
                } else {
                    var timeLeft by remember(tvCodeData) { mutableIntStateOf(tvCodeData?.expiresIn ?: 0) }

                    LaunchedEffect(tvCodeData) {
                        while (timeLeft > 0) {
                            delay(1000L)
                            timeLeft--
                        }
                        if (timeLeft == 0 && tvCodeData != null) {
                            onCancelCodePoll()
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            text = stringResource(id = R.string.tv_code_instructions),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                        Text(
                            text = tvCodeData?.code.orEmpty(),
                            style = MaterialTheme.typography.displayLarge.copy(letterSpacing = 8.sp),
                            fontWeight = FontWeight.Bold
                        )
                        if (tvCodeData != null) {
                            if (timeLeft > 0) {
                                Text(
                                    text = stringResource(id = R.string.tv_code_expiration, timeLeft),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 32.dp)
                                )
                            } else {
                                Text(
                                    text = stringResource(id = R.string.tv_code_expired),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
                                )
                                PrimaryButton(
                                    text = stringResource(id = R.string.request_new_code),
                                    onClick = onTvCodeTabSelected
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}