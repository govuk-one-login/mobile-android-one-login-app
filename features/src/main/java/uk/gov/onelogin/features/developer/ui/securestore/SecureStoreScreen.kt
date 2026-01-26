package uk.gov.onelogin.features.developer.ui.securestore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.features.BuildConfig
import uk.gov.android.ui.theme.m3.defaultColors
import uk.gov.android.ui.theme.m3.toMappedColors
import uk.gov.android.ui.theme.mediumPadding
import uk.gov.android.ui.theme.smallPadding

@Composable
fun SecureStoreScreen(viewModel: SecureStoreScreenViewModel = hiltViewModel()) {
    val overrideWallet by viewModel.overrideWallet.collectAsState()
    val enableDeletionFail by viewModel.enableDeletionFail.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(mediumPadding),
    ) {
        if (BuildConfig.FLAVOR == "build" || BuildConfig.FLAVOR == "staging") {
            Row(
                modifier = Modifier.padding(smallPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = "Override Delete Wallet Data",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color =
                        uk.gov.android.ui.theme.m3.Text.primary
                            .toMappedColors(),
                )
                Switch(
                    checked = overrideWallet,
                    onCheckedChange = { viewModel.setOverride(!overrideWallet) },
                    colors =
                        uk.gov.android.ui.theme.m3.Switch
                            .defaultColors(),
                )
            }
            Row(
                modifier = Modifier.padding(smallPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = "Enable local data deletion fail",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color =
                        uk.gov.android.ui.theme.m3.Text.primary
                            .toMappedColors(),
                )
                Switch(
                    checked = enableDeletionFail,
                    onCheckedChange = { viewModel.setDeletionFail(!enableDeletionFail) },
                    colors =
                        uk.gov.android.ui.theme.m3.Switch
                            .defaultColors(),
                )
            }
        }
    }
}
