package uk.gov.onelogin.developer.tabs.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.BuildConfig
import uk.gov.android.ui.theme.largePadding
import uk.gov.android.ui.theme.mediumPadding

@Composable
fun AppTabScreen(
    viewModel: AppTabScreenViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(mediumPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = buildAnnotatedString {
                append("App flavor: ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(BuildConfig.FLAVOR)
                }
            }
        )

        Text(text = "Version Name: ${BuildConfig.VERSION_NAME}")
        Text(text = "Version Code: ${BuildConfig.VERSION_CODE}")
        Text(
            modifier = Modifier.padding(top = largePadding),
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("App Info: \n")
                }
                append(viewModel.getAppInfoAsString())
            }
        )
    }
}
