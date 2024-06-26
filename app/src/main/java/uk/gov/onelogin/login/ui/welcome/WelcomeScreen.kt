package uk.gov.onelogin.login.ui.welcome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.R
import uk.gov.android.ui.components.content.GdsContentText
import uk.gov.android.ui.pages.LandingPage
import uk.gov.android.ui.pages.LandingPageParameters
import uk.gov.onelogin.developer.DeveloperTools

@Composable
fun WelcomeScreen(
    viewModel: WelcomeScreenViewModel = hiltViewModel(),
    navigateToOfflineErrorScreen: () -> Unit = { },
    shouldTryAgain: () -> Boolean = { false },
    openDeveloperPanel: () -> Unit = { }
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
                        R.string.app_signInBody
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
            onTopIconClick = {
                if (DeveloperTools.isDeveloperPanelEnabled()) {
                    openDeveloperPanel()
                }
            },
            primaryButtonText = R.string.app_signInButton,
            title = R.string.app_signInTitle,
            topIcon = R.drawable.app_icon,
            contentDescription = R.string.app_signInIconDescription
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
