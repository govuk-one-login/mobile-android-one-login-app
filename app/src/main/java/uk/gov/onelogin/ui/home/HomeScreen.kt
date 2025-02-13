package uk.gov.onelogin.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.R
import uk.gov.android.ui.theme.smallPadding
import uk.gov.onelogin.criorchestrator.features.resume.publicapi.ProveYourIdentityCard
import uk.gov.onelogin.criorchestrator.sdk.publicapi.rememberCriOrchestrator
import uk.gov.onelogin.developer.DeveloperTools
import uk.gov.onelogin.ui.components.TitledPage

@Composable
@Preview
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val httpClient = viewModel.httpClient
    val criOrchestratorComponent = rememberCriOrchestrator(httpClient)
    LaunchedEffect(Unit) {
        viewModel.getUiCardFlagState()
    }
    TitledPage(R.string.app_homeTitle) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .height(IntrinsicSize.Max)
        ) {
            if (viewModel.uiCardEnabled.collectAsState().value) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = smallPadding)
                        .padding(bottom = smallPadding)
                        .testTag(stringResource(R.string.appCriCardTestTag))
                ) {
                    ProveYourIdentityCard(
                        component = criOrchestratorComponent,
                        modifier = Modifier
                    )
                }
            }

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
