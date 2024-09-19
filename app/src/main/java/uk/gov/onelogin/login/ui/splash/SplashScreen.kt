package uk.gov.onelogin.login.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import uk.gov.android.onelogin.R
import uk.gov.android.ui.components.GdsHeading
import uk.gov.android.ui.components.HeadingParameters
import uk.gov.android.ui.components.HeadingSize
import uk.gov.android.ui.components.images.icon.IconParameters
import uk.gov.android.ui.components.m3.images.icon.GdsIcon
import uk.gov.android.ui.theme.mediumPadding
import uk.gov.onelogin.developer.DeveloperTools

@Preview
@Composable
fun SplashScreen(
    viewModel: SplashScreenViewModel = hiltViewModel(),
    fromLockScreen: Boolean = false
) {
    val context = LocalContext.current as FragmentActivity
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.govuk_blue)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GdsIcon(
            parameters = IconParameters(
                image = R.drawable.tudor_crown_with_gov_uk,
                backGroundColor = colorResource(id = R.color.govuk_blue),
                foreGroundColor = Color.White,
                modifier = Modifier
                    .weight(1F)
                    .clickable(enabled = DeveloperTools.isDeveloperPanelEnabled()) {
                        viewModel.navigateToDevPanel()
                    }
                    .testTag(stringResource(id = R.string.splashIconTestTag))
            )
        )
        if (viewModel.showUnlock.value) {
            GdsHeading(
                headingParameters = HeadingParameters(
                    size = HeadingSize.H3(),
                    text = R.string.app_unlockButton,
                    color = Color.White,
                    backgroundColor = colorResource(id = R.color.govuk_blue),
                    modifier = Modifier
                        .clickable {
                            viewModel.login(context, false)
                        }
                        .padding(bottom = mediumPadding)
                )
            )
        }
    }

    DisposableEffect(key1 = Unit) {
        lifecycleOwner.lifecycle.addObserver(viewModel)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(viewModel)
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (!viewModel.showUnlock.value) {
            viewModel.login(context, fromLockScreen)
        }
    }
}
