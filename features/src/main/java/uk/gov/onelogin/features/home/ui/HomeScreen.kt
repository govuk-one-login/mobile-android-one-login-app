package uk.gov.onelogin.features.home.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.GdsCard
import uk.gov.android.ui.theme.smallPadding
import uk.gov.onelogin.core.ui.pages.TitledLogoPage
import uk.gov.onelogin.criorchestrator.features.resume.publicapi.ProveYourIdentityCard
import uk.gov.onelogin.criorchestrator.sdk.publicapi.rememberCriOrchestrator
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorComponent
import uk.gov.onelogin.developer.DeveloperTools

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    analyticsViewModel: HomeScreenAnalyticsViewModel = hiltViewModel()
) {
    val criOrchestratorComponent = rememberCriOrchestrator(viewModel.criOrchestratorSdk)
    val isRefreshing = viewModel.isRefreshing.collectAsState()
    val pullToRefreshState = rememberPullRefreshState(
        // Allows for teh user to scroll up without triggering the refresh functionality
        // Makes the drag gesture to be at least 120.dp to trigger this
        refreshThreshold = 120.dp,
        refreshing = isRefreshing.value,
        onRefresh = { viewModel.refreshCriSdk() }
    )
    BackHandler { analyticsViewModel.trackBackButton() }
    LaunchedEffect(Unit) {
        viewModel.getUiCardFlagState()
        analyticsViewModel.trackScreen()
    }
    TitledLogoPage(R.drawable.ic_onelogin_title) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .pullRefresh(pullToRefreshState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Content(paddingValues, viewModel, criOrchestratorComponent)
            PullRefreshIndicator(
                isRefreshing.value,
                pullToRefreshState
            )
        }
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    viewModel: HomeScreenViewModel,
    criOrchestratorComponent: CriOrchestratorComponent
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = smallPadding)
            .consumeWindowInsets(paddingValues)
            .verticalScroll(rememberScrollState())
            .height(IntrinsicSize.Max)
            .windowInsetsPadding(WindowInsets.displayCutout)
    ) {
        val welcomeCardTitle = stringResource(R.string.app_welcomeTileHeader)
        val welcomeCardBody = stringResource(R.string.app_welcomeTileBody1)
        val proveIdentityCardTitle = stringResource(R.string.app_appPurposeTileHeader)
        val proveIdentityCardBody = stringResource(R.string.app_appPurposeTileBody1)
        if (viewModel.uiCardEnabled.collectAsState().value) {
            Row(
                modifier = Modifier
                    .testTag(stringResource(R.string.appCriCardTestTag))
                    .padding(top = smallPadding)
            ) {
                ProveYourIdentityCard(
                    component = criOrchestratorComponent,
                    modifier = Modifier
                )
            }
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
                onClick = { viewModel.openDevPanel() }
            ) {
                Text("Developer Panel")
            }
        }
    }
}

@Composable
private fun StaticContent(
    welcomeCardTitle: String,
    welcomeCardBody: String,
    proveIdentityCardTitle: String,
    proveIdentityCardBody: String
) {
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
