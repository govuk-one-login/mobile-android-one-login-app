package uk.gov.onelogin.login.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.ui.components.content.GdsContentText
import uk.gov.android.ui.pages.LandingPage
import uk.gov.android.ui.pages.LandingPageParameters
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.R
import uk.gov.onelogin.login.WelcomeScreenViewModel

@Composable
fun WelcomeScreen(
    viewModel: WelcomeScreenViewModel = hiltViewModel(),
    navigateToOfflineErrorScreen: () -> Unit = { },
    shouldTryAgain: () -> Boolean = { false }
) {
    val context = LocalContext.current
    LandingPage(
        landingPageParameters =
        LandingPageParameters(
            content =
            listOf(
                GdsContentText.GdsContentTextString(
                    text =
                    intArrayOf(
                        R.string.signInSubTitle
                    )
                )
            ),
            onPrimary = {
                if (viewModel.onlineChecker.isOnline()) {
                    viewModel.onPrimary(context)
                } else {
                    navigateToOfflineErrorScreen()
                }
            },
            primaryButtonText = R.string.signInButton,
            title = R.string.signInTitle,
            topIcon = R.drawable.app_icon
        )
    )
    LaunchedEffect(key1 = Unit) {
        if (!shouldTryAgain()) return@LaunchedEffect

        if (viewModel.onlineChecker.isOnline()) {
            viewModel.onPrimary(context)
        } else {
            navigateToOfflineErrorScreen()
        }
    }
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
