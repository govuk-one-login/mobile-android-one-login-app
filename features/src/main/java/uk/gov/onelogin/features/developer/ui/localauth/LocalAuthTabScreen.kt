package uk.gov.onelogin.features.developer.ui.localauth

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LocalAuthTabScreen(viewModel: LocalAuthTabScreenViewModel = hiltViewModel()) {
    val activity = LocalActivity.current as FragmentActivity
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
    ) {
        Button(
            onClick = { viewModel.triggerLocalAuthMock(activity, false) }
        ) {
            Text("Trigger Local Auth Manager error")
        }

        Button(
            onClick = { viewModel.triggerLocalAuthMock(activity, true) }
        ) {
            Text("Trigger Local Auth Manager biometrics opt in")
        }
    }
}
