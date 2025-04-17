package uk.gov.onelogin.features.home.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.GdsCard
import uk.gov.android.ui.theme.smallPadding
import uk.gov.onelogin.core.ui.pages.TitledPage
import uk.gov.onelogin.criorchestrator.features.dev.publicapi.DevMenuScreen
import uk.gov.onelogin.criorchestrator.features.resume.publicapi.ProveYourIdentityCard
import uk.gov.onelogin.criorchestrator.sdk.publicapi.rememberCriOrchestrator
import uk.gov.onelogin.developer.DeveloperTools

@Composable
@Preview
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    analyticsViewModel: HomeScreenAnalyticsViewModel = hiltViewModel()
) {
    val criSdk = viewModel.criOrchestratorSdk
    val criOrchestratorComponent = rememberCriOrchestrator(criSdk)
    val contentsCardTitle = stringResource(R.string.app_oneLoginCardTitle)
    val contentsCardBody = stringResource(R.string.app_oneLoginCardBody)
    val contentsCardLinkText = stringResource(R.string.app_oneLoginCardLink)
    val servicesUrl = stringResource(R.string.app_oneLoginCardLinkUrl)
    val uriHandler = LocalUriHandler.current
    var showDevMenu by remember { mutableStateOf(false) }
    var showDevMenuDialog by remember { mutableStateOf(false) }
    BackHandler { analyticsViewModel.trackBackButton() }
    LaunchedEffect(Unit) {
        viewModel.getUiCardFlagState()
        analyticsViewModel.trackScreen()
    }
    TitledPage(
        R.string.app_homeTitle,
        floatingActionButton = showDevMenu,
        onClick = {
            showDevMenu = true
        }
    ) { paddingValues ->
        if (showDevMenuDialog) {
            Dialog(properties = DialogProperties(usePlatformDefaultWidth = false), onDismissRequest = { showDevMenu = false }) {
                DevMenuScreen(criOrchestratorComponent)
            }
        }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = smallPadding)
                .verticalScroll(rememberScrollState())
                .height(IntrinsicSize.Max)
        ) {
            if (viewModel.uiCardEnabled.collectAsState().value) {
                Row(
                    modifier = Modifier
                        .testTag(stringResource(R.string.appCriCardTestTag))
                ) {
                    ProveYourIdentityCard(
                        component = criOrchestratorComponent,
                        modifier = Modifier
                    )
                }
            }
            GdsCard(
                title = contentsCardTitle,
                body = contentsCardBody,
                buttonText = contentsCardLinkText,
                displayPrimary = false,
                showSecondaryIcon = true,
                onClick = {
                    analyticsViewModel.trackLink()
                    uriHandler.openUri(servicesUrl)
                },
                modifier = Modifier
                    .padding(top = smallPadding)
                    .testTag(stringResource(R.string.yourServicesCardTestTag))
            )
            if (DeveloperTools.isDeveloperPanelEnabled()) {
                TextButton(
                    onClick = { viewModel.openDevPanel() }
                ) {
                    Text("Developer Panel")
                }
            }
        }
    }
}
