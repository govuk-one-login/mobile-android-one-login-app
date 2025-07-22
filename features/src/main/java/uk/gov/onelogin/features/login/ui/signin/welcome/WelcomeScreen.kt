package uk.gov.onelogin.features.login.ui.signin.welcome

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.button.ButtonType
import uk.gov.android.ui.componentsv2.button.GdsButton
import uk.gov.android.ui.componentsv2.heading.GdsHeading
import uk.gov.android.ui.patterns.centrealignedscreen.CentreAlignedScreen
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.smallPadding
import uk.gov.android.ui.theme.util.UnstableDesignSystemAPI
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
        val intent = result.data
        if (intent != null) {
            viewModel.handleActivityResult(intent = intent, activity = context)
        } else {
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

@OptIn(UnstableDesignSystemAPI::class)
@Composable
@Suppress("LongMethod")
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
            title = {
                GdsHeading(
                    text = title
                )
            },
            image = {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.app_icon),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().clearAndSetSemantics { }
                )
            },
            body = {
                item {
                    Text(
                        text = content[0],
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = smallPadding)
                    )
                }
                item {
                    Text(
                        text = content[1],
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = smallPadding)
                    )
                }
            },
            primaryButton = {
                GdsButton(
                    text = buttonText,
                    buttonType = ButtonType.Primary,
                    onClick = onSignIn,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            secondaryButton = {
                if (DeveloperTools.isDeveloperPanelEnabled()) {
                    GdsButton(
                        text = devButtonText,
                        buttonType = ButtonType.Secondary,
                        onClick = openDevMenu,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun WelcomePreview() {
    WelcomeBody()
}
