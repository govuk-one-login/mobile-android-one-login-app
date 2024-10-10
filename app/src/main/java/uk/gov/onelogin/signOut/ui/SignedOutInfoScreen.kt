package uk.gov.onelogin.signOut.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.R
import uk.gov.android.ui.components.content.GdsContentText
import uk.gov.android.ui.pages.LandingPage
import uk.gov.android.ui.pages.LandingPageParameters
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.smallPadding
import uk.gov.onelogin.login.ui.welcome.WelcomeScreenViewModel

@Composable
fun SignedOutInfoScreen(
    loginViewModel: WelcomeScreenViewModel = hiltViewModel(),
    signOutViewModel: SignedOutInfoViewModel = hiltViewModel(),
    shouldTryAgain: () -> Boolean = { false }
) {
    val activity = LocalContext.current as FragmentActivity
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

        handleLogin(
            loginViewModel,
            signOutViewModel,
            launcher,
            activity
        )
    }
    SignedOutInfoBody {
        handleLogin(
            loginViewModel,
            signOutViewModel,
            launcher,
            activity
        )
    }
}

private fun handleLogin(
    loginViewModel: WelcomeScreenViewModel,
    signOutViewModel: SignedOutInfoViewModel,
    launcher: ActivityResultLauncher<Intent>,
    activity: FragmentActivity
) {
    if (loginViewModel.onlineChecker.isOnline()) {
        signOutViewModel.checkPersistentId(activity) {
            loginViewModel.onPrimary(launcher)
        }
    } else {
        loginViewModel.navigateToOfflineError()
    }
}

@Composable
internal fun SignedOutInfoBody(
    onPrimary: () -> Unit
) {
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
            onPrimary = onPrimary
        )
    )
}

@PreviewLightDark
@PreviewFontScale
@PreviewScreenSizes
@Composable
internal fun SignedOutInfoPreview() {
    GdsTheme {
        SignedOutInfoBody {}
    }
}
