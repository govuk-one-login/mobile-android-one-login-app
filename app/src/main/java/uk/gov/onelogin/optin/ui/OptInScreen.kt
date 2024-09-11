package uk.gov.onelogin.optin.ui

import android.net.Uri
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
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.R
import uk.gov.android.ui.components.m3.buttons.ButtonParameters
import uk.gov.android.ui.components.m3.buttons.ButtonType
import uk.gov.android.ui.components.m3.buttons.GdsButton
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.mediumPadding
import uk.gov.android.ui.theme.smallPadding
import uk.gov.android.ui.theme.xsmallPadding

@Composable
fun OptInScreen(
    viewModel: OptInViewModel = hiltViewModel<OptInViewModel>(),
    onComplete: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val state = viewModel.uiState.collectAsState(OptInUIState.PreChoice)
    GdsTheme {
        OptInBody(
            uiState = state.value,
            onPrivacyNotice = { uri ->
                uriHandler.openUri(uri.toString())
            },
            onShare = {
                viewModel.optIn()
                onComplete()
            },
            onDoNotShare = {
                viewModel.optOut()
                onComplete()
            }
        )
    }
}

@Composable
internal fun OptInBody(
    uiState: OptInUIState,
    onPrivacyNotice: (Uri) -> Unit,
    onShare: () -> Unit,
    onDoNotShare: () -> Unit
) {
    val scrollState = rememberScrollState()
    val url = stringResource(id = R.string.privacy_notice_url)
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
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
        Text(
            modifier = Modifier
                .padding(all = smallPadding),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(id = R.string.app_analyticsPermissionBody)
        )
        PrivacyNotice(
            modifier = Modifier
                .padding(horizontal = smallPadding),
            privacyNoticeString = stringResource(id = R.string.app_privacyNoticeLink),
            onPrivacyNotice = { onPrivacyNotice(Uri.parse(url)) }
        )
        Spacer(modifier = Modifier.weight(1f))
        GdsButton(
            buttonParameters = ButtonParameters(
                modifier = Modifier
                    .padding(horizontal = smallPadding)
                    .padding(top = xsmallPadding)
                    .fillMaxWidth(),
                text = R.string.app_shareAnalyticsButton,
                buttonType = ButtonType.PRIMARY(),
                textStyle = MaterialTheme.typography.labelMedium,
                onClick = onShare,
                enabled = uiState.hasButtonsOn
            )
        )
        GdsButton(
            buttonParameters = ButtonParameters(
                modifier = Modifier
                    .padding(horizontal = smallPadding, vertical = xsmallPadding)
                    .fillMaxWidth(),
                text = R.string.app_doNotShareAnalytics,
                buttonType = ButtonType.SECONDARY(),
                textStyle = MaterialTheme.typography.labelMedium,
                onClick = onDoNotShare,
                enabled = uiState.hasButtonsOn
            )
        )
    }
}

@PreviewLightDark
@PreviewFontScale
@PreviewScreenSizes
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
