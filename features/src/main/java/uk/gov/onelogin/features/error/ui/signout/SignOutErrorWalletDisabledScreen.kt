package uk.gov.onelogin.features.error.ui.signout

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlinx.collections.immutable.persistentListOf
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.patterns.centrealignedscreen.CentreAlignedScreenBodyContent
import uk.gov.android.ui.patterns.centrealignedscreen.CentreAlignedScreenButton
import uk.gov.android.ui.patterns.errorscreen.ErrorScreen
import uk.gov.android.ui.patterns.errorscreen.ErrorScreenIcon
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.android.ui.theme.meta.ScreenPreview
import uk.gov.onelogin.core.ui.pages.EdgeToEdgePage

@Composable
fun SignOutErrorWalletDisabledScreen(onExitAppClicked: () -> Unit) {
    GdsTheme {
        EdgeToEdgePage { _ ->
            ErrorScreen(
                icon = ErrorScreenIcon.ErrorIcon,
                title = stringResource(R.string.app_signOutErrorTitle),
                body = persistentListOf(
                    CentreAlignedScreenBodyContent.Text(
                        bodyText = stringResource(R.string.app_signOutErrorBody)
                    )
                ),
                primaryButton =
                CentreAlignedScreenButton(
                    text = stringResource(R.string.app_exitButton),
                    onClick = onExitAppClicked
                )
            )
        }
    }
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
fun Preview() {
    SignOutErrorWalletDisabledScreen { }
}
