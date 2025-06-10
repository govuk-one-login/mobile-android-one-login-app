package uk.gov.onelogin.features.signout.ui.success

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.collections.immutable.persistentListOf
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.patterns.centrealignedscreen.CentreAlignedScreen
import uk.gov.android.ui.patterns.centrealignedscreen.CentreAlignedScreenBodyContent
import uk.gov.android.ui.patterns.centrealignedscreen.CentreAlignedScreenButton
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview
import uk.gov.onelogin.core.ui.pages.EdgeToEdgePage
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag

@Composable
fun SignOutSuccess(
    viewModel: SignOutSuccessViewModel = hiltViewModel(),
    analyticsViewModel: SignOutSuccessAnalyticsViewModel = hiltViewModel()
) {
    val walletFeatureFlag = viewModel.featureFlags[WalletFeatureFlag.ENABLED]
    val context = LocalContext.current

    BackHandler(enabled = true) {
        analyticsViewModel.trackBackPress()
        // Close the app
        (context as? Activity)?.finish()
    }

    EdgeToEdgePage { _ ->
        analyticsViewModel.trackWalletCopyScreen(walletFeatureFlag)
        SignOutConfirmationBody(walletFeatureFlag) {
            analyticsViewModel.trackPrimaryButton()
            viewModel.navigateStart()
        }
    }
}

@Composable
fun SignOutConfirmationBody(
    walletEnabled: Boolean,
    onPrimary: () -> Unit
) {
    val content = if (walletEnabled) {
        listOf(
            stringResource(R.string.app_signOutWalletBody),
            stringResource(R.string.app_signOutWalletBody2)
        )
    } else {
        listOf(
            stringResource(R.string.app_signOutBody),
            stringResource(R.string.app_signOutBody2)
        )
    }
    val buttonText = stringResource(R.string.app_signOutSuccessButton)
    CentreAlignedScreen(
        title = stringResource(R.string.app_signOutTitle),
        modifier = Modifier.fillMaxSize(),
        body = persistentListOf(
            CentreAlignedScreenBodyContent.Text(content[0]),
            CentreAlignedScreenBodyContent.Text(content[1])
        ),
        primaryButton = CentreAlignedScreenButton(
            text = buttonText,
            onClick = onPrimary
        )
    )
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun SignOutSuccessPreview() {
    GdsTheme {
        SignOutConfirmationBody(false) {}
    }
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun SignOutSuccessWalletPreview() {
    GdsTheme {
        SignOutConfirmationBody(true) {}
    }
}
