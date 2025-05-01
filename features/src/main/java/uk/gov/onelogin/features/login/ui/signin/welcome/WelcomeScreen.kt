package uk.gov.onelogin.features.login.ui.signin.welcome

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.button.ButtonType
import uk.gov.android.ui.componentsv2.button.GdsButton
import uk.gov.android.ui.componentsv2.heading.GdsHeading
import uk.gov.android.ui.componentsv2.heading.GdsHeadingAlignment
import uk.gov.android.ui.componentsv2.heading.GdsHeadingStyle
import uk.gov.android.ui.componentsv2.images.GdsVectorImage
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.mediumPadding
import uk.gov.android.ui.theme.smallPadding
import uk.gov.android.ui.theme.util.UnstableDesignSystemAPI
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview
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
    val loading = viewModel.loading.collectAsState()
    val context = LocalActivity.current as FragmentActivity
    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { intent ->
                    viewModel.handleActivityResult(intent = intent, activity = context)
                }
            }
        }
    WelcomeBody(
        onSignIn = {
            if (viewModel.onlineChecker.isOnline()) {
                viewModel.onPrimary(launcher)
                analyticsViewModel.trackSignIn()
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
    if (loading.value) {
        LoadingScreen(loadingAnalyticsViewModel) {
            viewModel.abortLogin(launcher)
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

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        analyticsViewModel.trackWelcomeView()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.stopLoading()
    }
}

@OptIn(UnstableDesignSystemAPI::class)
@Composable
internal fun WelcomeBody(
    onSignIn: () -> Unit = { },
    onTopIconClick: () -> Unit = { }
) {
    val icon = ImageVector.vectorResource(R.drawable.app_icon)
    val iconContentDesc = stringResource(R.string.app_signInIconDescription)
    val title = stringResource(R.string.app_signInTitle)
    val content = listOf(
        stringResource(R.string.app_signInBody1),
        stringResource(R.string.app_signInBody2)
    )
    val buttonText = stringResource(R.string.app_signInButton)
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(bottom = mediumPadding, top = mediumPadding),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
        ) {
            GdsVectorImage(
                image = icon,
                contentDescription = iconContentDesc,
                modifier = Modifier
                    .padding(bottom = mediumPadding)
                    .clickable(enabled = true) { onTopIconClick() }
            )
            GdsHeading(
                text = title,
                style = GdsHeadingStyle.LargeTitle,
                textAlign = GdsHeadingAlignment.CenterAligned,
                modifier = Modifier.padding(
                    end = smallPadding,
                    start = smallPadding,
                    bottom = mediumPadding
                )
            )
            content.forEach {
                Text(
                    text = it,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = mediumPadding)
                )
            }
        }
        GdsButton(
            text = buttonText,
            buttonType = ButtonType.Primary,
            onClick = onSignIn,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = smallPadding,
                    end = smallPadding
                )
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
