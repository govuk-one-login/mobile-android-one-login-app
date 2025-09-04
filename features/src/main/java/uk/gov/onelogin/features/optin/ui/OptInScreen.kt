package uk.gov.onelogin.features.optin.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.button.ButtonType
import uk.gov.android.ui.componentsv2.button.GdsButton
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.mediumPadding
import uk.gov.android.ui.theme.smallPadding
import uk.gov.android.ui.theme.xsmallPadding
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview
import uk.gov.onelogin.core.ui.pages.EdgeToEdgePage

@Composable
fun OptInScreen(viewModel: OptInViewModel = hiltViewModel()) {
    val uriHandler = LocalUriHandler.current
    val state = viewModel.uiState.collectAsState(OptInUIState.PreChoice)
    BackHandler(enabled = true) { viewModel.goToSignIn() }
    GdsTheme {
        OptInBody(
            uiState = state.value,
            onPrivacyNotice = { uri ->
                uriHandler.openUri(uri)
            },
            onShare = {
                viewModel.optIn()
                viewModel.goToSignIn()
            },
            onDoNotShare = {
                viewModel.optOut()
                viewModel.goToSignIn()
            }
        )
    }
}

@Composable
internal fun OptInBody(
    uiState: OptInUIState,
    onPrivacyNotice: (String) -> Unit,
    onShare: () -> Unit,
    onDoNotShare: () -> Unit
) {
    val scrollState = rememberScrollState()
    val url = stringResource(id = R.string.privacy_notice_url)
    EdgeToEdgePage {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            OptInHeader()
            OptInContent(onPrivacyNotice, url)
            Spacer(modifier = Modifier.weight(1f))
            OptInButtons(onShare, uiState, onDoNotShare)
        }
    }
}

@Composable
private fun OptInHeader() {
    Text(
        modifier = Modifier
            .semantics { heading() }
            .padding(horizontal = smallPadding)
            .padding(top = mediumPadding),
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.headlineLarge,
        text = stringResource(id = R.string.app_analyticsPermissionTitle),
        textAlign = TextAlign.Start
    )
}

@Composable
private fun OptInContent(
    onPrivacyNotice: (String) -> Unit,
    url: String
) {
    Text(
        modifier = Modifier
            .padding(all = smallPadding),
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.bodyLarge,
        text = stringResource(id = R.string.app_analyticsPermissionBody)
    )
    PrivacyNotice(
        modifier = Modifier
            .padding(horizontal = smallPadding)
            .semantics(mergeDescendants = true) {},
        style = MaterialTheme.typography.bodyLarge,
        privacyNoticeLink = stringResource(id = R.string.app_privacyNoticeLink),
        onPrivacyNotice = { onPrivacyNotice(url) }
    )
}

@Composable
private fun OptInButtons(
    onShare: () -> Unit,
    uiState: OptInUIState,
    onDoNotShare: () -> Unit
) {
    GdsButton(
        text = stringResource(R.string.app_shareAnalyticsButton),
        buttonType = ButtonType.Primary,
        onClick = onShare,
        modifier = Modifier
            .padding(horizontal = smallPadding)
            .padding(top = xsmallPadding)
            .fillMaxWidth(),
        enabled = uiState.hasButtonsOn
    )
    GdsButton(
        text = stringResource(R.string.app_doNotShareAnalytics),
        buttonType = ButtonType.Secondary,
        onClick = onDoNotShare,
        modifier = Modifier
            .padding(horizontal = smallPadding, vertical = xsmallPadding)
            .fillMaxWidth(),
        enabled = uiState.hasButtonsOn
    )
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun OptInPreview() {
    GdsTheme {
        OptInBody(
            uiState = OptInUIState.PreChoice,
            onPrivacyNotice = {},
            onShare = {},
            onDoNotShare = {}
        )
    }
}
