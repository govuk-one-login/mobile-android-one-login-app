package uk.gov.onelogin.login.ui.welcome

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import uk.gov.android.onelogin.R
import uk.gov.android.ui.components.content.GdsContentText
import uk.gov.android.ui.pages.LandingPage
import uk.gov.android.ui.pages.LandingPageParameters
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.developer.DeveloperTools

@Composable
fun WelcomeScreen(
    viewModel: WelcomeScreenViewModel = hiltViewModel(),
    shouldTryAgain: () -> Boolean = { false }
) {
    val analytics: SignInAnalyticsViewModel = hiltViewModel()
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                viewModel.handleActivityResult(intent = intent)
            }
        }
    }
    WelcomeBody(
        onSignIn = {
            if (viewModel.onlineChecker.isOnline()) {
                viewModel.onPrimary(launcher)
                analytics.trackSignIn()
            } else {
                viewModel.navigateToOfflineError()
            }
        },
        onTopIconClick = {
            if (DeveloperTools.isDeveloperPanelEnabled()) {
                viewModel.navigateToDevPanel()
            }
        }
    )

    LaunchedEffect(key1 = Unit) {
        if (!shouldTryAgain()) return@LaunchedEffect
        if (viewModel.onlineChecker.isOnline()) {
            viewModel.onPrimary(launcher)
        } else {
            viewModel.navigateToOfflineError()
        }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { analytics.trackWelcomeView() }
}

@Composable
internal fun WelcomeBody(
    onSignIn: () -> Unit = { },
    onTopIconClick: () -> Unit = { }
) {
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
            onPrimary = onSignIn,
            onTopIconClick = onTopIconClick,
            primaryButtonText = R.string.app_signInButton,
            title = R.string.app_signInTitle,
            topIcon = R.drawable.app_icon,
            contentDescription = R.string.app_signInIconDescription
        )
    )
}

@PreviewLightDark
@PreviewFontScale
@PreviewScreenSizes
@Composable
internal fun WelcomePreview() {
    GdsTheme {
        WelcomeBody()
    }
}
