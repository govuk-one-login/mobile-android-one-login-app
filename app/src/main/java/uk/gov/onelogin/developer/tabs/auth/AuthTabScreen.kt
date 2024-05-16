package uk.gov.onelogin.developer.tabs.auth

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.R
import uk.gov.android.ui.components.m3.buttons.ButtonParameters
import uk.gov.android.ui.components.m3.buttons.ButtonType
import uk.gov.android.ui.components.m3.buttons.GdsButton

@Composable
fun AuthTabScreen(
    viewModel: AuthTabScreenViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val happyApiResponse by viewModel.happyHelloWorldResponse
        val happyCallLoading by viewModel.happyCallLoading
        val sadApiResponse by viewModel.sadHelloWorldResponse
        val sadCallLoading by viewModel.sadCallLoading
        buttonRow(
            buttonText = R.string.app_helloworld_happy_button,
            buttonLoading = happyCallLoading,
            apiResponse = happyApiResponse
        ) {
            viewModel.makeHappyHelloWorldCall()
        }
        buttonRow(
            buttonText = R.string.app_helloworld_sad_button,
            buttonLoading = sadCallLoading,
            apiResponse = sadApiResponse
        ) {
            viewModel.makeSadHelloWorldCall()
        }
    }
}

@Composable
private fun buttonRow(
    @StringRes
    buttonText: Int,
    buttonLoading: Boolean,
    apiResponse: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!buttonLoading) {
            GdsButton(
                buttonParameters = ButtonParameters(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = buttonText,
                    buttonType = ButtonType.PRIMARY(),
                    onClick = {
                        onClick()
                    }
                )
            )
        } else {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = buildAnnotatedString {
                append("Api response: ")
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(apiResponse)
                }
            }
        )
    }
}
