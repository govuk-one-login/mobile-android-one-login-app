package uk.gov.onelogin.features.home.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.GdsCard
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.smallPadding
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview
import uk.gov.onelogin.core.ui.pages.TitledLogoPage
import uk.gov.onelogin.criorchestrator.features.resume.publicapi.ProveYourIdentityCard
import uk.gov.onelogin.criorchestrator.sdk.publicapi.rememberCriOrchestrator
import uk.gov.onelogin.developer.DeveloperTools

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    analyticsViewModel: HomeScreenAnalyticsViewModel = hiltViewModel()
) {
    val criOrchestratorGraph = rememberCriOrchestrator(viewModel.criOrchestratorSdk)

    BackHandler { analyticsViewModel.trackBackButton() }
    LaunchedEffect(Unit) {
        viewModel.getUiCardFlagState()
        analyticsViewModel.trackScreen()
    }
    HomeScreenBody(
        uiCardEnabled = viewModel.uiCardEnabled.collectAsState().value,
        content = {
            Row(
                modifier = Modifier
                    .testTag(stringResource(R.string.appCriCardTestTag))
                    .padding(top = smallPadding)
            ) {
                ProveYourIdentityCard(
                    graph = criOrchestratorGraph,
                    modifier = Modifier
                )
            }
        },
        openDevPanel = { viewModel.openDevPanel() }
    )
}

@Composable
private fun HomeScreenBody(
    uiCardEnabled: Boolean,
    content: @Composable () -> Unit = {},
    openDevPanel: () -> Unit = {}
) {
    val welcomeCardTitle = stringResource(R.string.app_welcomeTileHeader)
    val welcomeCardBody = stringResource(R.string.app_welcomeTileBody1)
    val proveIdentityCardTitle = stringResource(R.string.app_appPurposeTileHeader)
    val proveIdentityCardBody = stringResource(R.string.app_appPurposeTileBody1)
    TitledLogoPage(R.drawable.ic_onelogin_title) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = smallPadding)
                .consumeWindowInsets(paddingValues)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.displayCutout)
        ) {
            if (uiCardEnabled) {
                content()
            }
            AddCard(
                cardTitle = welcomeCardTitle,
                cardBody = welcomeCardBody,
                testTag = R.string.welcomeCardTestTag
            )
            AddCard(
                cardTitle = proveIdentityCardTitle,
                cardBody = proveIdentityCardBody,
                testTag = R.string.proveIdentityCardTestTag
            )
            if (DeveloperTools.isDeveloperPanelEnabled()) {
                TextButton(
                    onClick = { openDevPanel() }
                ) {
                    Text("Developer Panel")
                }
            }
        }
    }
}

@Composable
private fun AddCard(
    cardTitle: String,
    cardBody: String,
    @StringRes testTag: Int
) {
    GdsCard(
        title = cardTitle,
        body = cardBody,
        displayPrimary = false,
        shadow = 0.dp,
        onClick = {},
        modifier = Modifier
            .padding(top = smallPadding)
            .testTag(stringResource(testTag))
    )
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun HomeScreenPreview() {
    GdsTheme {
        HomeScreenBody(
            uiCardEnabled = false
        )
    }
}
