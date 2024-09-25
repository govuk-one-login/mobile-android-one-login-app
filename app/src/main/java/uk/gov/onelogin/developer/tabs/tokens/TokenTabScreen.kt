package uk.gov.onelogin.developer.tabs.tokens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.R
import uk.gov.android.ui.components.m3.buttons.ButtonParameters
import uk.gov.android.ui.components.m3.buttons.ButtonType
import uk.gov.android.ui.components.m3.buttons.GdsButton

@Composable
fun TokenTabScreen(
    viewModel: TokenTabScreenViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GdsButton(
                buttonParameters = ButtonParameters(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = R.string.app_developer_reset_access_token_button,
                    buttonType = ButtonType.PRIMARY(),
                    onClick = {
                        viewModel.resetAccessToken()
                    }
                )
            )
        }
    }
}
