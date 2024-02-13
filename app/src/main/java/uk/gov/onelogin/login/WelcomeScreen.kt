package uk.gov.onelogin.login

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.ui.components.content.GdsContentText
import uk.gov.android.ui.pages.LandingPage
import uk.gov.android.ui.pages.LandingPageParameters
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.R

@Composable
fun WelcomeScreen(
    viewModel: WelcomeScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LandingPage(
        landingPageParameters = LandingPageParameters(
            content = listOf(
                GdsContentText.GdsContentTextString(
                    text = intArrayOf(
                        R.string.signInSubTitle
                    )
                )
            ),
            onPrimary = {
                viewModel.onPrimary(context)
            },
            primaryButtonText = R.string.signInButton,
            title = R.string.signInTitle,
            topIcon = R.drawable.app_icon
        )
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun Preview() {
    GdsTheme {
        WelcomeScreen()
    }
}
