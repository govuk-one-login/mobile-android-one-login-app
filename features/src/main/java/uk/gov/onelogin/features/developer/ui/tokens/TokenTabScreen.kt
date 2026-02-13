package uk.gov.onelogin.features.developer.ui.tokens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import uk.gov.android.ui.theme.mediumPadding
import uk.gov.android.ui.theme.smallPadding

@Composable
fun TokenTabScreen(viewModel: TokenTabScreenViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val persistentId by viewModel.persistentId.collectAsState()
    val accessTokenExpiry by viewModel.accessTokenExpiry.collectAsState()
    val refreshTokenExpiry by viewModel.refreshTokenExpiry.collectAsState()
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(largePadding),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
    ) {
        AccessTokenSection(viewModel, accessTokenExpiry, context)
        RefreshTokenSection(viewModel, refreshTokenExpiry, context)
        PersistentIdSection(persistentId, viewModel)
    }
}

@Composable
private fun PersistentIdSection(
    persistentId: String,
    viewModel: TokenTabScreenViewModel,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = mediumPadding),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier =
                Modifier
                    .weight(1F)
                    .padding(end = smallPadding),
            text = "Persistent ID: ${persistentId.ifEmpty { "<Empty>" }}",
            color = MaterialTheme.colorScheme.onBackground,
        )
        GdsButton(
            text = stringResource(R.string.app_developer_reset_persistent_id_button),
            buttonType = ButtonTypeV2.Primary(),
            onClick = {
                viewModel.resetPersistentId()
            },
            modifier = Modifier.weight(1F),
        )
    }
}

@Composable
private fun AccessTokenSection(
    viewModel: TokenTabScreenViewModel,
    accessTokenExpiry: String,
    context: Context,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            modifier =
                Modifier
                    .weight(1F)
                    .padding(end = smallPadding),
            text = "Access Token Expiry: $accessTokenExpiry",
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
    ButtonRow(
        text = stringResource(R.string.set_access_token_empty_button),
    ) {
        viewModel.updateAccessTokenToNull()
        Toast
            .makeText(context, "Access token expiry set to null!", Toast.LENGTH_SHORT)
            .show()
    }
    ButtonRow(
        text = stringResource(R.string.app_developer_reset_access_token_button),
    ) {
        viewModel.resetAccessTokenExp()
        Toast
            .makeText(context, "Access token expiry set to now!", Toast.LENGTH_SHORT)
            .show()
    }
    ButtonRow(
        text = stringResource(R.string.app_developer_access_token_button_expire_30_sec),
    ) {
        viewModel.setAccessTokenExpireTo30Seconds()
        Toast
            .makeText(
                context,
                "Access token expiry set to 30 seconds from now!",
                Toast.LENGTH_SHORT,
            ).show()
    }
}

@Composable
private fun RefreshTokenSection(
    viewModel: TokenTabScreenViewModel,
    refreshTokenExpiry: String,
    context: Context,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            modifier =
                Modifier
                    .weight(1F)
                    .padding(end = smallPadding),
            text = "Refresh Token Expiry: $refreshTokenExpiry",
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
    ButtonRow(
        text = stringResource(R.string.set_refresh_token_empty_button),
    ) {
        viewModel.updateRefreshTokenToNull()
        Toast
            .makeText(context, "Refresh token expiry set to null!", Toast.LENGTH_SHORT)
            .show()
    }
    ButtonRow(
        text = stringResource(R.string.app_developer_reset_refresh_token_button),
    ) {
        viewModel.resetRefreshTokenExp()
        Toast
            .makeText(context, "Refresh token expiry set to now!", Toast.LENGTH_SHORT)
            .show()
    }
    ButtonRow(
        text = stringResource(R.string.app_developer_refresh_token_button_expire_30_sec),
    ) {
        viewModel.setRefreshTokenExpireTo30Seconds()
        Toast
            .makeText(
                context,
                "Refresh token expiry set to 30 seconds from now!",
                Toast.LENGTH_SHORT,
            ).show()
    }
    ButtonRow(
        text = stringResource(R.string.app_developer_refresh_token_button_expire_5_min),
    ) {
        viewModel.setRefreshTokenExpireTo5Minutes()
        Toast
            .makeText(
                context,
                "Refresh token expiry set to 5 minutes from now!",
                Toast.LENGTH_SHORT,
            ).show()
    }
}

@Composable
private fun ButtonRow(
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = mediumPadding),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GdsButton(
            text = text,
            buttonType = ButtonTypeV2.Primary(),
            onClick = onClick,
        )
    }
}
