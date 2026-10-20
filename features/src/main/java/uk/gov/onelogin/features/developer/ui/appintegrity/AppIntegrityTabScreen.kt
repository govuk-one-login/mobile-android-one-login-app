package uk.gov.onelogin.features.developer.ui.appintegrity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.ui.componentsv2.button.ButtonTypeV2
import uk.gov.android.ui.componentsv2.button.GdsButton
import uk.gov.android.ui.componentsv2.button.GdsButtonDefaults
import uk.gov.android.ui.theme.m3.toMappedColors
import uk.gov.android.ui.theme.smallPadding

@Composable
fun AppIntegrityTabScreen(viewModel: AppIntegrityTabViewModel = hiltViewModel()) {
    Column(
        modifier =
            Modifier
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .fillMaxSize()
                .padding(smallPadding),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        Row(
            modifier = Modifier.padding(smallPadding),
        ) {
            Text(
                text =
                    buildAnnotatedString {
                        AppendBold("App Check")
                    },
            )
        }

        DataItem(
            action1 = { viewModel.resetAttestation() },
            action2 = { viewModel.setFakeAttestation() },
            result = viewModel.clientAttestation,
            buttonText1 = "Remove attestation",
            buttonText2 = "Set fake attestation",
        )

        DataItem(
            action1 = { viewModel.resetAttestationExpiry() },
            action2 = { viewModel.setFutureAttestationExpiry() },
            result = viewModel.clientAttestationExpiry,
            buttonText1 = "Reset attestation expiry time",
            buttonText2 = "Set expiry in future",
        )

        ActionItemIcon(
            action = { viewModel.getToken() },
            result = viewModel.tokenResponse,
            buttonText = "Get Firebase Token",
            isIconTrailing = true,
        )

        ActionItem(
            action = { viewModel.makeNetworkCall() },
            result = viewModel.networkResponse,
            buttonText = "Make Mobile Backend Api Call",
        )

        ActionItemIcon(
            action = { viewModel.getClientAttestation() },
            result = viewModel.clientAttestationResult,
            buttonText = "Get Client Attestation",
            isIconTrailing = false,
        )

        ActionItemFullWidth(
            action = { viewModel.generatePoP() },
            result = viewModel.proofOfPossessionResult,
            buttonText = "Generate Proof Of Possession",
        )
    }
}

@Composable
private fun ActionItem(
    action: () -> Unit,
    result: MutableState<String>,
    buttonText: String,
    isEnabled: Boolean = true,
    buttonTypeV2: ButtonTypeV2 = ButtonTypeV2.Primary(),
) {
    Row {
        GdsButton(
            text = buttonText,
            buttonType = buttonTypeV2,
            modifier = Modifier.padding(start = smallPadding),
            onClick = action,
            enabled = isEnabled,
        )
    }
    Row(modifier = Modifier.padding(all = smallPadding)) {
        Text(
            text = result.value,
            color =
                uk.gov.android.ui.theme.m3.Text.primary
                    .toMappedColors(),
        )
    }
}

@Composable
private fun ActionItemIcon(
    action: () -> Unit,
    result: MutableState<String>,
    buttonText: String,
    isIconTrailing: Boolean,
) {
    Row {
        GdsButton(
            text = buttonText,
            buttonType =
                ButtonTypeV2.Icon(
                    buttonColors = GdsButtonDefaults.defaultSecondaryColors(),
                    isIconTrailing = isIconTrailing,
                ),
            modifier = Modifier.padding(start = smallPadding),
            onClick = action,
        )
    }
    Row(modifier = Modifier.padding(all = smallPadding)) {
        Text(
            text = result.value,
            color =
                uk.gov.android.ui.theme.m3.Text.primary
                    .toMappedColors(),
        )
    }
}

@Composable
private fun ActionItemFullWidth(
    action: () -> Unit,
    result: MutableState<String>,
    buttonText: String,
) {
    Row {
        GdsButton(
            text = buttonText,
            buttonType = ButtonTypeV2.Secondary(),
            modifier =
                Modifier
                    .padding(start = smallPadding)
                    .fillMaxWidth(),
            onClick = action,
        )
    }
    Row(modifier = Modifier.padding(all = smallPadding)) {
        Text(
            text = result.value,
            color =
                uk.gov.android.ui.theme.m3.Text.primary
                    .toMappedColors(),
        )
    }
}

@Composable
private fun DataItem(
    action1: () -> Unit,
    action2: () -> Unit,
    result: MutableState<String>,
    buttonText1: String,
    buttonText2: String,
) {
    Row {
        GdsButton(
            text = buttonText1,
            buttonType = ButtonTypeV2.Destructive(),
            modifier = Modifier.padding(start = smallPadding).weight(1F),
            onClick = action1,
        )
        GdsButton(
            text = buttonText2,
            buttonType = ButtonTypeV2.Secondary(),
            modifier = Modifier.padding(start = smallPadding).weight(1F),
            onClick = action2,
        )
    }
    Row(modifier = Modifier.padding(all = smallPadding)) {
        Text(
            text = result.value,
            color =
                uk.gov.android.ui.theme.m3.Text.primary
                    .toMappedColors(),
        )
    }
}

@Composable
private fun AnnotatedString.Builder.AppendBold(token: String) {
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append(token)
    }
}
