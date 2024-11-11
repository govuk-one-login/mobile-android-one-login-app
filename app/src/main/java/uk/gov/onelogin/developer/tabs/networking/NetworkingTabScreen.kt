package uk.gov.onelogin.developer.tabs.networking

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.ui.theme.smallPadding

@Composable
fun NetworkingTabScreen(
    viewModel: NetworkingViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(smallPadding),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Row {
            Text(
                text = buildAnnotatedString {
                    appendBold("App Check")
                }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = buildAnnotatedString {
                    appendBold("Token: ")
                }
            )
            Button(
                modifier = Modifier.padding(start = smallPadding),
                onClick = {
                    viewModel.getToken()
                }
            ) {
                Text(text = "Get Firebase Token")
            }
        }
        Row(modifier = Modifier.padding(all = smallPadding)) {
            Text(text = viewModel.tokenResponse.value)
        }
        Row {
            Button(
                modifier = Modifier.padding(start = smallPadding),
                onClick = {
                    viewModel.makeNetworkCall()
                }
            ) {
                Text(text = "Make Mobile Backend Api Call")
            }
        }
        Row(modifier = Modifier.padding(all = smallPadding)) {
            Text(text = viewModel.networkResponse.value)
        }

        Row {
            Button(
                modifier = Modifier.padding(start = smallPadding),
                onClick = {
                    viewModel.startAppIntegrityCheck()
                }
            ) {
                Text(text = "Start App Integrity Check")
            }
        }
        Row(modifier = Modifier.padding(all = smallPadding)) {
            Text(text = viewModel.appIntegrityResult.value)
        }
    }
}

@Composable
private fun AnnotatedString.Builder.appendBold(token: String) {
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append(token)
    }
}
