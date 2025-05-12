package uk.gov.onelogin.features.developer.ui.criorchestratormenu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.components.GdsHeading
import uk.gov.android.ui.components.HeadingParameters
import uk.gov.android.ui.components.HeadingSize
import uk.gov.android.ui.theme.mediumPadding
import uk.gov.onelogin.criorchestrator.features.dev.publicapi.DevMenuScreen
import uk.gov.onelogin.criorchestrator.sdk.publicapi.rememberCriOrchestrator

@Composable
fun CriOrchestratorDevMenuScreen(viewModel: CriOrchestratorDevMenuScreenViewModel = hiltViewModel()) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(mediumPadding)
    ) {
        GdsHeading(
            headingParameters =
                HeadingParameters(
                    size = HeadingSize.H2(),
                    text = R.string.app_developer_features_title
                )
        )
        val criOrchestratorComponent = rememberCriOrchestrator(viewModel.criOrchestratorSdk)
        DevMenuScreen(criOrchestratorComponent)
    }
}
