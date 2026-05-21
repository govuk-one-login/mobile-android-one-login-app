package uk.gov.onelogin.features.signout.ui.info

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import kotlinx.collections.immutable.persistentListOf
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.patterns.centrealignedscreen.CentreAlignedScreen
import uk.gov.android.ui.patterns.centrealignedscreen.CentreAlignedScreenBodyContent
import uk.gov.android.ui.patterns.centrealignedscreen.CentreAlignedScreenButton
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview
import uk.gov.onelogin.core.ui.pages.EdgeToEdgePage
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreen
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreenAnalyticsViewModel
import uk.gov.onelogin.features.login.LoginViewModel

@Composable
fun SignedOutInfoScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    viewModel: SignedOutInfoViewModel = hiltViewModel(),
    analyticsViewModel: SignedOutInfoAnalyticsViewModel = hiltViewModel(),
    loadingAnalyticsViewModel: LoadingScreenAnalyticsViewModel = hiltViewModel(),
    shouldTryAgain: () -> Boolean = { false },
) {
    val loading by loginViewModel.loading.collectAsState()
    val activity = LocalActivity.current as FragmentActivity
    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result: ActivityResult ->
            loginViewModel.handleLoginActivityResult(
                result = result,
                isReAuth = viewModel.shouldReAuth(),
                activity = activity,
            )
        }

    LaunchedEffect(key1 = Unit) {
        viewModel.resetTokens()

        if (!shouldTryAgain()) return@LaunchedEffect

        startLogin(
            loginViewModel,
            launcher,
        )
    }

    EdgeToEdgePage { _ ->
        if (loading) {
            LoadingScreen(loadingAnalyticsViewModel) {}
        } else {
            SignedOutInfoBody {
                analyticsViewModel.trackReAuth()
                startLogin(
                    loginViewModel,
                    launcher,
                )
            }
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        analyticsViewModel.trackSignOutInfoView()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        loginViewModel.stopLoading()
    }
}

private fun startLogin(
    loginViewModel: LoginViewModel,
    launcher: ActivityResultLauncher<Intent>,
) {
    loginViewModel.startLoginActivity(launcher, true)
}

@Composable
internal fun SignedOutInfoBody(onPrimary: () -> Unit) {
    val content =
        listOf(
            stringResource(R.string.app_youveBeenSignedOutBody1),
            stringResource(R.string.app_youveBeenSignedOutBody2),
        )
    val buttonText = stringResource(R.string.app_SignInWithGovUKOneLoginButton)
    CentreAlignedScreen(
        title = stringResource(R.string.app_youveBeenSignedOutTitle),
        modifier = Modifier.fillMaxSize(),
        body =
            persistentListOf(
                CentreAlignedScreenBodyContent.Text(content[0]),
                CentreAlignedScreenBodyContent.Text(content[1]),
            ),
        primaryButton =
            CentreAlignedScreenButton(
                text = buttonText,
                onClick = onPrimary,
            ),
    )
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun SignedOutInfoPreview() {
    GdsTheme {
        SignedOutInfoBody {}
    }
}
