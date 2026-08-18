package uk.gov.onelogin.features.home.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
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
import uk.gov.android.ui.patterns.utils.ModifierExtensions.keyboardScroll
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.smallPadding
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview
import uk.gov.onelogin.core.ui.pages.TitledLogoPage
import uk.gov.onelogin.criorchestrator.features.resume.publicapi.ProveYourIdentityCard
import uk.gov.onelogin.criorchestrator.sdk.publicapi.rememberCriOrchestrator
import uk.gov.onelogin.developer.DeveloperTools
import uk.gov.onelogin.features.home.idcheck.ui.HowToProveYourIdentityCard

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    analyticsViewModel: HomeScreenAnalyticsViewModel = hiltViewModel(),
) {
    val criOrchestratorComponent = rememberCriOrchestrator(viewModel.criOrchestratorSdk)

    BackHandler { analyticsViewModel.trackBackButton() }
    LaunchedEffect(Unit) {
        viewModel.resetWalletDeepLinkPath()
        viewModel.getUiCardFlagState()
        analyticsViewModel.trackScreen()
    }
    HomeScreenBody(
        proveYourIdentityCardEnabled = viewModel.uiCardEnabled.collectAsState().value,
        proveYourIdentityCard = {
            Row(
                modifier =
                    Modifier
                        .testTag(stringResource(R.string.appCriCardTestTag))
                        .padding(top = smallPadding),
            ) {
                ProveYourIdentityCard(
                    graph = criOrchestratorComponent,
                    modifier = Modifier,
                )
            }
        },
        openDevPanel = { viewModel.openDevPanel() },
    )
}

@Suppress(
    // The 'prove your identity' card slot isn't designed for generic 'content'
    "ComposableLambdaParameterNaming",
)
@Composable
private fun HomeScreenBody(
    openDevPanel: () -> Unit = {},
    proveYourIdentityCardEnabled: Boolean,
    proveYourIdentityCard: @Composable () -> Unit = {},
) {
    TitledLogoPage(R.drawable.ic_onelogin_title) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .padding(smallPadding)
                    .consumeWindowInsets(paddingValues)
                    .verticalScroll(scrollState)
                    .keyboardScroll(scrollState)
                    .windowInsetsPadding(WindowInsets.displayCutout),
            verticalArrangement = Arrangement.spacedBy(smallPadding)
        ) {
            if (proveYourIdentityCardEnabled) {
                proveYourIdentityCard()
            }
            WelcomeCard()
            HowToProveYourIdentityCard()
            if (DeveloperTools.IS_DEVELOPER_PANEL_ENABLED) {
                TextButton(
                    onClick = { openDevPanel() },
                ) {
                    Text("Developer Panel")
                }
            }
        }
    }
}

@Composable
private fun WelcomeCard(
    modifier: Modifier = Modifier
) = GdsCard(
    title = stringResource(R.string.app_welcomeTileHeader),
    body = stringResource(R.string.app_welcomeTileBody1),
    displayPrimary = false,
    shadow = 0.dp,
    onClick = {},
    modifier = modifier
        .testTag(stringResource(R.string.welcomeCardTestTag)),
)

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun HomeScreenPreview() {
    GdsTheme {
        HomeScreenBody(
            proveYourIdentityCardEnabled = false,
        )
    }
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun HomeScreenWithProveYourIdentityCardPreview() {
    GdsTheme {
        HomeScreenBody(
            proveYourIdentityCardEnabled = true,
            proveYourIdentityCard = {
                GdsCard(
                    title = "Prove your identity",
                    body = "This is a placeholder",
                    buttonText = "Continue proving your identity",
                    onClick = {}
                )
            },
        )
    }
}
