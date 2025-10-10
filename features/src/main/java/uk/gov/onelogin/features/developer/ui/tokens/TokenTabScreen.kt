package uk.gov.onelogin.features.developer.ui.tokens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.button.ButtonTypeV2
import uk.gov.android.ui.componentsv2.button.GdsButton
import uk.gov.android.ui.theme.largePadding
import uk.gov.android.ui.theme.m3.toMappedColors
import uk.gov.android.ui.theme.mediumPadding
import uk.gov.android.ui.theme.smallPadding

@Composable
fun TokenTabScreen(viewModel: TokenTabScreenViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val persistentId by viewModel.persistentId.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(largePadding),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        ButtonRow(
            text = stringResource(R.string.app_developer_reset_access_token_button)
        ) {
            viewModel.resetAccessToken()
            Toast.makeText(context, "Token expiry set to now!", Toast.LENGTH_SHORT)
                .show()
        }
        ButtonRow(
            text = stringResource(R.string.app_developer_access_token_button_expire_45_sec)
        ) {
            viewModel.setAccessTokenExpireTo30Seconds()
            Toast.makeText(
                context,
                "Token expiry set to 30 seconds from now!",
                Toast.LENGTH_SHORT
            ).show()
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = mediumPadding),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1F)
                    .padding(end = smallPadding),
                text = "Persistent ID: ${persistentId.ifEmpty { "<Empty>" }}",
                color = uk.gov.android.ui.theme.m3.Text.primary.toMappedColors()
            )
            GdsButton(
                text = stringResource(R.string.app_developer_reset_persistent_id_button),
                buttonType = ButtonTypeV2.Primary(),
                onClick = {
                    viewModel.resetPersistentId()
                },
                modifier = Modifier.weight(1F)
            )
        }
    }
}

@Composable
private fun ButtonRow(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = mediumPadding),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GdsButton(
            text = text,
            buttonType = ButtonTypeV2.Primary(),
            onClick = onClick
        )
    }
}
