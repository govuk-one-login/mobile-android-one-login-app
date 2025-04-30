package uk.gov.onelogin.features.developer.ui.criorchestratormenu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
            headingParameters = HeadingParameters(
                size = HeadingSize.H2(),
                text = R.string.app_developer_features_title
            )
        )
        var subjectToken by rememberSaveable { mutableStateOf("") }

        LaunchedEffect(subjectToken) {
//            onSubUpdateRequest(subjectToken.takeIf { it.isNotBlank() })
        }

        TextField(
            value = subjectToken,
            onValueChange = { subjectToken = it },
            label = {
                Text("Subject token")
            },
        )
        val criOrchestratorComponent = rememberCriOrchestrator(viewModel.criOrchestratorSdk)
        DevMenuScreen(criOrchestratorComponent)
    }
}
