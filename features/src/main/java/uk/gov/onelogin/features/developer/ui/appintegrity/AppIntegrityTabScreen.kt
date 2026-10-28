package uk.gov.onelogin.features.developer.ui.appintegrity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.android.ui.componentsv2.button.ButtonTypeV2
import uk.gov.android.ui.componentsv2.button.GdsButton
import uk.gov.android.ui.componentsv2.heading.GdsHeading
import uk.gov.android.ui.componentsv2.heading.GdsHeadingAlignment
import uk.gov.android.ui.componentsv2.heading.GdsHeadingStyle
import uk.gov.android.ui.patterns.leftalignedscreen.LeftAlignedScreen
import uk.gov.android.ui.theme.m3.Switch
import uk.gov.android.ui.theme.m3.defaultColors
import uk.gov.android.ui.theme.smallPadding

@Suppress("LongMethod")
@Composable
fun AppIntegrityTabScreen(viewModel: AppIntegrityTabViewModel = hiltViewModel()) =
    LeftAlignedScreen(
        title = { horizontalPadding ->
            GdsHeading(
                "App integrity",
                textAlign = GdsHeadingAlignment.LeftAligned,
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )
        },
        body = { horizontalPadding ->
            item {
                GdsHeading(
                    text = "Settings",
                    style = GdsHeadingStyle.Title2,
                    textAlign = GdsHeadingAlignment.LeftAligned,
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                )
            }
            item {
                ToggleItem(
                    text = "Force 'intermittent' errors",
                    checked = false,
                    onToggle = {
                        // TODO: DCMAW-18663
                    },
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                )
            }

            item {
                GdsHeading(
                    text = "Actions",
                    style = GdsHeadingStyle.Title2,
                    textAlign = GdsHeadingAlignment.LeftAligned,
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                )
            }
            item {
                ActionItem(
                    action = { viewModel.getClientAttestationAndCache() },
                    result = viewModel.clientAttestationResult,
                    buttonText = "Get Client Attestation",
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                )
            }

            item {
                DataItem(
                    action1 = { viewModel.resetAttestation() },
                    action2 = { viewModel.setFakeAttestation() },
                    result = viewModel.clientAttestation,
                    buttonText1 = "Remove attestation",
                    buttonText2 = "Set fake attestation",
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                )
            }

            item {
                DataItem(
                    action1 = { viewModel.resetAttestationExpiry() },
                    action2 = { viewModel.setFutureAttestationExpiry() },
                    result = viewModel.clientAttestationExpiry,
                    buttonText1 = "Reset attestation expiry time",
                    buttonText2 = "Set expiry in future",
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                )
            }

            item {
                ActionItem(
                    action = { viewModel.getToken() },
                    result = viewModel.tokenResponse,
                    buttonText = "Get Firebase Token",
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                )
            }

            item {
                ActionItem(
                    action = { viewModel.getClientAttestationFromApi() },
                    result = viewModel.clientAttestationApiResponse,
                    "Call Client Attestation API",
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                )
            }

            item {
                ActionItem(
                    action = { viewModel.generatePoP() },
                    result = viewModel.proofOfPossessionResult,
                    buttonText = "Generate Proof Of Possession",
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                )
            }
        },
    )

@Composable
private fun ActionItem(
    action: () -> Unit,
    result: MutableState<String>,
    buttonText: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    buttonTypeV2: ButtonTypeV2 = ButtonTypeV2.Primary(),
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(smallPadding),
) {
    Row {
        GdsButton(
            text = buttonText,
            buttonType = buttonTypeV2,
            onClick = action,
            enabled = isEnabled,
        )
    }
    if (result.value.isNotBlank()) {
        Row {
            Text(text = result.value)
        }
    }
}

@Composable
private fun DataItem(
    action1: () -> Unit,
    action2: () -> Unit,
    result: MutableState<String>,
    buttonText1: String,
    buttonText2: String,
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(smallPadding),
) {
    Row {
        GdsButton(
            text = buttonText1,
            buttonType = ButtonTypeV2.Destructive(),
            modifier = Modifier.weight(1F),
            onClick = action1,
        )
        GdsButton(
            text = buttonText2,
            buttonType = ButtonTypeV2.Secondary(),
            modifier = Modifier.weight(1F),
            onClick = action2,
        )
    }

    if (result.value.isNotBlank()) {
        Row {
            Text(text = result.value)
        }
    }
}

@Composable
private fun ToggleItem(
    text: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
) {
    Text(
        modifier = Modifier.weight(1F),
        text = text,
    )
    Switch(
        checked = checked,
        onCheckedChange = { onToggle(it) },
        colors = Switch.defaultColors(),
    )
}
