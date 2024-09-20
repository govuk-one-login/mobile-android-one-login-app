package uk.gov.onelogin.login.ui.welcome

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.R
import uk.gov.android.ui.components.content.GdsContentText
import uk.gov.android.ui.pages.LandingPage
import uk.gov.android.ui.pages.LandingPageParameters
import uk.gov.onelogin.developer.DeveloperTools

@Composable
fun WelcomeScreen(
    viewModel: WelcomeScreenViewModel = hiltViewModel(),
    shouldTryAgain: () -> Boolean = { false }
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                viewModel.handleActivityResult(intent = intent)
            }
        }
    }

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
                    viewModel.onPrimary(launcher)
                } else {
                    viewModel.navigateToOfflineError()
                }
            },
            onTopIconClick = {
                if (DeveloperTools.isDeveloperPanelEnabled()) {
                    viewModel.navigateToDevPanel()
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
            viewModel.onPrimary(launcher)
        } else {
            viewModel.navigateToOfflineError()
        }
    }
}
