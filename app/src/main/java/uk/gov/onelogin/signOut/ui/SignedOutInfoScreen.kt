package uk.gov.onelogin.signOut.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.R
import uk.gov.android.ui.components.content.GdsContentText
import uk.gov.android.ui.pages.LandingPage
import uk.gov.android.ui.pages.LandingPageParameters
import uk.gov.android.ui.theme.smallPadding
import uk.gov.onelogin.login.ui.welcome.WelcomeScreenViewModel

@Composable
fun SignedOutInfoScreen(
    loginViewModel: WelcomeScreenViewModel = hiltViewModel(),
    signOutViewModel: SignedOutInfoViewModel = hiltViewModel(),
    shouldTryAgain: () -> Boolean = { false }
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                loginViewModel.handleActivityResult(
                    intent = intent,
                    isReAuth = signOutViewModel.shouldReAuth()
                )
                signOutViewModel.saveTokens()
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        signOutViewModel.resetTokens()

        if (!shouldTryAgain()) return@LaunchedEffect

        if (loginViewModel.onlineChecker.isOnline()) {
            loginViewModel.onPrimary(launcher)
        } else {
            loginViewModel.navigateToOfflineError()
        }
    }

    LandingPage(
        landingPageParameters = LandingPageParameters(
            title = R.string.app_youveBeenSignedOutTitle,
            titleBottomPadding = smallPadding,
            content = listOf(
                GdsContentText.GdsContentTextString(
                    intArrayOf(R.string.app_youveBeenSignedOutBody1)
                ),
                GdsContentText.GdsContentTextString(
                    intArrayOf(R.string.app_youveBeenSignedOutBody2)
                )
            ),
            contentInternalPadding = PaddingValues(bottom = smallPadding),
            primaryButtonText = R.string.app_SignInWithGovUKOneLoginButton,
            onPrimary = {
                if (loginViewModel.onlineChecker.isOnline()) {
                    loginViewModel.onPrimary(launcher)
                } else {
                    loginViewModel.navigateToOfflineError()
                }
            }
        )
    )
}
