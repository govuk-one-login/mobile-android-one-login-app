package uk.gov.onelogin.features.developer.ui.criorchestratormenu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.heading.GdsHeading
import uk.gov.android.ui.componentsv2.heading.GdsHeadingStyle
import uk.gov.android.ui.theme.mediumPadding
import uk.gov.android.ui.theme.util.UnstableDesignSystemAPI
import uk.gov.onelogin.criorchestrator.features.dev.publicapi.DevMenuScreen
import uk.gov.onelogin.criorchestrator.sdk.publicapi.rememberCriOrchestrator

@OptIn(UnstableDesignSystemAPI::class)
@Composable
fun CriOrchestratorDevMenuScreen(
    viewModel: CriOrchestratorDevMenuScreenViewModel = hiltViewModel()
) {
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(mediumPadding)
    ) {
        GdsHeading(
            text = stringResource(R.string.app_developer_features_title),
            style = GdsHeadingStyle.Title3
        )
        val criOrchestratorComponent = rememberCriOrchestrator(viewModel.criOrchestratorSdk)
        DevMenuScreen(criOrchestratorComponent)
    }
}
