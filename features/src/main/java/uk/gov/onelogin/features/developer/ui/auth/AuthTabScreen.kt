package uk.gov.onelogin.features.developer.ui.auth

import androidx.activity.compose.LocalActivity
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.button.ButtonTypeV2
import uk.gov.android.ui.componentsv2.button.GdsButton
import uk.gov.android.ui.theme.m3.toMappedColors
import uk.gov.android.ui.theme.smallPadding
import uk.gov.onelogin.core.ui.components.EmailSection

@Composable
fun AuthTabScreen(viewModel: AuthTabScreenViewModel = hiltViewModel()) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.navigationBarsPadding().fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AuthTokensSection(viewModel)
        OpenIdAuthSection(viewModel)
    }
}

@Composable
private fun AuthTokensSection(viewModel: AuthTabScreenViewModel) {
    val tokens = viewModel.getTokens()
    val email = viewModel.email
    Text(
        text = "Authentication Tokens",
        style = MaterialTheme.typography.titleMedium,
        color = uk.gov.android.ui.theme.m3.Text.primary.toMappedColors()
    )
    HorizontalDivider()
    EmailSection(email)
    AccessTokenSection(tokens)
    HorizontalDivider()
    IdTokenSection(tokens)
    HorizontalDivider()
    RefreshTokenSection(tokens, viewModel)
    HorizontalDivider()
}

@Composable
private fun RefreshTokenSection(
    tokens: TokenResponse?,
    viewModel: AuthTabScreenViewModel
) {
    Text(
        text = "Refresh Token",
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp),
        color = uk.gov.android.ui.theme.m3.Text.primary.toMappedColors()
    )
    HorizontalDivider(Modifier.padding(start = 16.dp))
    Text(
        text = if (tokens?.refreshToken != null) {
            "Something went wrong - the refresh token is saved in memory: ${tokens.refreshToken}"
        } else {
            "Refresh token in temporary memory is null - this is expected behaviour."
        },
        modifier = Modifier
            .padding(
                all = 16.dp
            ),
        color = uk.gov.android.ui.theme.m3.Text.primary.toMappedColors()
    )
    val isRefreshTokenSaved by viewModel.isRefreshTokenSaved
    val context = LocalActivity.current as FragmentActivity
    Text(
        "Is refresh token saved: $isRefreshTokenSaved",
        modifier = Modifier.padding(bottom = smallPadding)
    )
    GdsButton(
        text = stringResource(R.string.check_refresh_token_saved_button),
        buttonType = ButtonTypeV2.Primary(),
        onClick = {
            viewModel.checkRefreshTokenSaved(context)
        },
        enabled = true,
        modifier = Modifier.padding(bottom = smallPadding)
    )
}

@Composable
private fun IdTokenSection(tokens: TokenResponse?) {
    Text(
        text = "ID Token",
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp),
        color = uk.gov.android.ui.theme.m3.Text.primary.toMappedColors()
    )
    HorizontalDivider(Modifier.padding(start = 16.dp))
    Text(
        text = tokens?.idToken ?: "No id token set!",
        modifier = Modifier
            .padding(16.dp),
        color = uk.gov.android.ui.theme.m3.Text.primary.toMappedColors()
    )
}

@Composable
private fun AccessTokenSection(tokens: TokenResponse?) {
    Text(
        text = "Access Token",
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp),
        color = uk.gov.android.ui.theme.m3.Text.primary.toMappedColors()
    )
    HorizontalDivider(Modifier.padding(start = 16.dp))
    Text(
        tokens?.accessToken ?: "No access token set!",
        modifier = Modifier
            .padding(16.dp),
        color = uk.gov.android.ui.theme.m3.Text.primary.toMappedColors()
    )
}

@Composable
private fun OpenIdAuthSection(viewModel: AuthTabScreenViewModel) {
    val happyApiResponse by viewModel.happyHelloWorldResponse
    val happyCallLoading by viewModel.happyCallLoading
    val authFailingApiResponse by viewModel.authFailingHelloWorldResponse
    val authFailingCallLoading by viewModel.authFailingCallLoading
    val serviceFailingApiResponse by viewModel.serviceFailingHelloWorldResponse
    val serviceFailingCallLoading by viewModel.serviceFailingCallLoading
    ButtonRow(
        buttonText = R.string.app_helloworld_happy_button,
        buttonLoading = happyCallLoading,
        apiResponse = happyApiResponse
    ) {
        viewModel.makeHappyHelloWorldCall()
    }
    ButtonRow(
        buttonText = R.string.app_helloworld_auth_failing_button,
        buttonLoading = authFailingCallLoading,
        apiResponse = authFailingApiResponse
    ) {
        viewModel.makeAuthFailingHelloWorldCall()
    }
    ButtonRow(
        buttonText = R.string.app_helloworld_service_failing_button,
        buttonLoading = serviceFailingCallLoading,
        apiResponse = serviceFailingApiResponse
    ) {
        viewModel.makeServiceFailingHelloWorldCall()
    }
}

@Composable
private fun ButtonRow(
    @StringRes
    buttonText: Int,
    buttonLoading: Boolean,
    apiResponse: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier =
        Modifier.fillMaxWidth()
            .padding(vertical = smallPadding),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GdsButton(
            text = stringResource(buttonText),
            buttonType = uk.gov.android.ui.componentsv2.button.ButtonTypeV2.Primary(),
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.padding(bottom = 8.dp),
            loading = buttonLoading
        )
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = buildAnnotatedString {
                append("Api response: ")
                withStyle(
                    style =
                    SpanStyle(
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(apiResponse)
                }
            },
            color = uk.gov.android.ui.theme.m3.Text.primary.toMappedColors()
        )
    }
}
