package uk.gov.onelogin.features.login.ui.signin.welcome

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
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
import uk.gov.android.ui.patterns.centrealignedscreen.CentreAlignedScreenImage
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview
import uk.gov.onelogin.core.ui.pages.EdgeToEdgePage
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreen
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreenAnalyticsViewModel
import uk.gov.onelogin.developer.DeveloperTools

@Composable
fun WelcomeScreen(
    viewModel: WelcomeScreenViewModel = hiltViewModel(),
    analyticsViewModel: SignInAnalyticsViewModel = hiltViewModel(),
    loadingAnalyticsViewModel: LoadingScreenAnalyticsViewModel = hiltViewModel(),
    shouldTryAgain: () -> Boolean = { false }
) {
    val loading by viewModel.loading.collectAsState()
    val context = LocalActivity.current as FragmentActivity
    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->

            handleResult(result, viewModel, context)
        }

    if (loading) {
        LoadingScreen(loadingAnalyticsViewModel) {
            viewModel.abortLogin(launcher)
        }
    } else {
        EdgeToEdgePage { _ ->
            WelcomeBody(
                onSignIn = {
                    handleScreenExit(viewModel, analyticsViewModel, launcher)
                },
                openDevMenu = { viewModel.navigateToDevPanel() }
            )
        }
    }

    BackHandler(enabled = true) {
        context.finishAndRemoveTask()
    }
    LaunchedEffect(key1 = Unit) {
        if (!shouldTryAgain()) return@LaunchedEffect
        if (viewModel.onlineChecker.isOnline()) {
            viewModel.onPrimary(launcher)
        } else {
            viewModel.navigateToOfflineError()
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        if (!loading) {
            analyticsViewModel.trackWelcomeView()
        }
        viewModel.stopLoading()
    }
}

private fun handleResult(
    result: ActivityResult,
    viewModel: WelcomeScreenViewModel,
    context: FragmentActivity
) {
    if (result.resultCode == Activity.RESULT_OK) {
        result.data?.let { intent ->
            viewModel.handleActivityResult(intent = intent, activity = context)
        }?.run {
            viewModel.stopLoading()
        }
    } else {
        viewModel.stopLoading()
    }
}

private fun handleScreenExit(
    viewModel: WelcomeScreenViewModel,
    analyticsViewModel: SignInAnalyticsViewModel,
    launcher: ActivityResultLauncher<Intent>
) {
    if (viewModel.onlineChecker.isOnline()) {
        viewModel.onPrimary(launcher)
    } else {
        viewModel.navigateToOfflineError()
    }
    analyticsViewModel.trackSignIn()
}

@Composable
internal fun WelcomeBody(
    onSignIn: () -> Unit = { },
    openDevMenu: () -> Unit = { }
) {
    val title = stringResource(R.string.app_signInTitle)
    val content = listOf(
        stringResource(R.string.app_signInBody1),
        stringResource(R.string.app_signInBody2)
    )
    val buttonText = stringResource(R.string.app_signInButton)
    val devButtonText = stringResource(R.string.app_developer_button)
    GdsTheme {
        CentreAlignedScreen(
            title = title,
            modifier = Modifier.fillMaxSize(),
            image = CentreAlignedScreenImage(
                image = R.drawable.app_icon,
                // This is not required - see https://govukverify.atlassian.net/browse/DCMAW-12974 comment from UCD
                description = ""
            ),
            body = persistentListOf(
                CentreAlignedScreenBodyContent.Text(content[0]),
                CentreAlignedScreenBodyContent.Text(content[1])
            ),
            primaryButton = CentreAlignedScreenButton(
                text = buttonText,
                onClick = onSignIn
            ),
            secondaryButton = if (DeveloperTools.isDeveloperPanelEnabled()) {
                CentreAlignedScreenButton(
                    text = devButtonText,
                    onClick = openDevMenu
                )
            } else {
                null
            }
        )
    }
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun WelcomePreview() {
    GdsTheme {
        WelcomeBody()
    }
}
