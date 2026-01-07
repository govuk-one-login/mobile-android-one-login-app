package uk.gov.onelogin.features.developer.ui.app

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import uk.gov.android.onelogin.features.BuildConfig
import uk.gov.android.ui.componentsv2.button.ButtonTypeV2
import uk.gov.android.ui.componentsv2.button.GdsButton
import uk.gov.android.ui.componentsv2.status.GdsStatusOverlay
import uk.gov.android.ui.theme.largePadding
import uk.gov.android.ui.theme.m3.Switch
import uk.gov.android.ui.theme.m3.defaultColors
import uk.gov.android.ui.theme.m3.toMappedColors
import uk.gov.android.ui.theme.mediumPadding
import uk.gov.android.ui.theme.smallPadding
import uk.gov.android.ui.theme.spacingDouble
import uk.gov.onelogin.features.appinfo.data.model.AppInfoData

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Suppress("LongMethod")
@Composable
fun AppTabScreen(viewModel: AppTabScreenViewModel = hiltViewModel()) {
    val data = viewModel.appInfo.collectAsState()
    val statusOverlayState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(viewModel.appInfo) {
        viewModel.getAppInfo()
    }
    var overlayMessage by remember { mutableStateOf("This is a message") }
    Scaffold(
        snackbarHost = {
            GdsStatusOverlay(
                hostState = statusOverlayState,
                modifier = Modifier.padding(horizontal = spacingDouble)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize().navigationBarsPadding()
                .padding(mediumPadding),
            verticalArrangement = Arrangement.Center
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = buildAnnotatedString {
                            append("App flavor: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(BuildConfig.FLAVOR)
                            }
                        },
                        color = uk.gov.android.ui.theme.m3.Text.primary.toMappedColors()
                    )
                    Text(
                        text = "Version Name: ${viewModel.version}",
                        color = uk.gov.android.ui.theme.m3.Text.primary.toMappedColors()
                    )
                }
            }
            Text(
                modifier = Modifier.padding(top = largePadding),
                style = MaterialTheme.typography.titleMedium,
                text = "App Info",
                textAlign = TextAlign.Left,
                color = uk.gov.android.ui.theme.m3.Text.primary.toMappedColors()
            )
            data.value?.let { androidInfoData ->
                AppInfoView(
                    androidInfoData.apps.android,
                    viewModel
                )
            }
            Row(
                modifier = Modifier.padding(top = largePadding)
            ) {
                OutlinedTextField(
                    value = overlayMessage,
                    onValueChange = { overlayMessage = it },
                    label = { Text(text = "Status Overlay message") },
                    colors = textFieldDefaultColors(),
                    modifier = Modifier.weight(TEXTFIELD_WEIGHT)
                )
                GdsButton(
                    text = "Display overlay",
                    buttonType = ButtonTypeV2.Primary(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = smallPadding)
                        .weight(BUTTON_WEIGHT),
                    onClick = {
                        scope.launch {
                            statusOverlayState.showSnackbar(overlayMessage)
                        }
                    }
                )
            }
        }
    }
}

@Composable
@Suppress("LongMethod")
private fun AppInfoView(
    appInfoData: AppInfoData.AppInfo,
    viewModel: AppTabScreenViewModel
) {
    var minimumVersion by remember { mutableStateOf(appInfoData.minimumVersion) }
    var appAvailable by remember { mutableStateOf(appInfoData.available) }
    var appCheckEnabled by remember { mutableStateOf(appInfoData.featureFlags.appCheckEnabled) }
    OutlinedTextField(
        value = minimumVersion,
        onValueChange = { minimumVersion = it },
        label = { Text(text = "Minimum version") },
        colors = textFieldDefaultColors()
    )
    Text("App Available", color = uk.gov.android.ui.theme.m3.Text.primary.toMappedColors())
    Switch(
        checked = appAvailable,
        onCheckedChange = { appAvailable = it },
        colors = Switch.defaultColors()
    )
    Text(
        "Feature Flag - App Check Enabled",
        color = uk.gov.android.ui.theme.m3.Text.primary.toMappedColors()
    )
    Switch(
        checked = appCheckEnabled,
        onCheckedChange = { appCheckEnabled = it },
        colors = Switch.defaultColors()
    )
    Button(
        onClick = {
            val updatedData =
                AppInfoData(
                    AppInfoData.App(
                        AppInfoData.AppInfo(
                            minimumVersion = minimumVersion,
                            available = appAvailable,
                            featureFlags =
                            AppInfoData.FeatureFlags(
                                appCheckEnabled = appCheckEnabled
                            )
                        )
                    )
                )
            Log.d("UpdatedAppInfo", "$updatedData")
            viewModel.updateAppInfoData(updatedData)
        }
    ) {
        Text(
            "Update App Info Data - Local Source",
            color = uk.gov.android.ui.theme.m3.Text.primary.toMappedColors()
        )
    }
}

@Composable
private fun textFieldDefaultColors() = TextFieldDefaults.colors().copy(
    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
    focusedContainerColor = MaterialTheme.colorScheme.background,
    unfocusedContainerColor = MaterialTheme.colorScheme.background
)

const val TEXTFIELD_WEIGHT = 0.7f
const val BUTTON_WEIGHT = 0.3f
