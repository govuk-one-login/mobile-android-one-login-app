package uk.gov.onelogin.features.error.ui.auth

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.components.content.GdsContentText
import uk.gov.android.ui.pages.LandingPage
import uk.gov.android.ui.pages.LandingPageParameters
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.smallPadding
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport
import uk.gov.onelogin.core.ui.meta.ScreenPreview

@Composable
fun AuthErrorScreen(viewModel: AuthErrorViewModel = hiltViewModel()) {
    GdsTheme {
        AuthErrorBody(viewModel.walletEnabled) {
            viewModel.navigateToSignIn()
        }
    }
}

@Composable
private fun AuthErrorBody(
    walletEnabled: Boolean,
    goToSignIn: () -> Unit = {}
) {
    val title = R.string.app_dataDeletedErrorTitle
    val buttonText = R.string.app_dataDeletedButton
    val contentDescription = R.string.app_dataDeletedError_ContentDescription
    val bodyContent =
        if (walletEnabled) {
            AuthScreenBodyContent(
                body1 = R.string.app_dataDeletedBody1,
                body2 = R.string.app_dataDeletedBody2,
                body3 = R.string.app_dataDeletedBody3
            )
        } else {
            AuthScreenBodyContent(
                body1 = R.string.app_dataDeletedBody1_no_wallet,
                body2 = R.string.app_dataDeletedBody2_no_wallet,
                body3 = R.string.app_dataDeletedBody3_no_wallet
            )
        }
    LandingPage(
        landingPageParameters =
            LandingPageParameters(
                topIcon = uk.gov.android.ui.components.R.drawable.ic_error,
                iconColor = MaterialTheme.colorScheme.onBackground,
                contentDescription = contentDescription,
                title = title,
                titleBottomPadding = smallPadding,
                content =
                    listOf(
                        GdsContentText.GdsContentTextString(
                            text =
                                intArrayOf(
                                    bodyContent.body1
                                )
                        ),
                        GdsContentText.GdsContentTextString(
                            text =
                                intArrayOf(
                                    bodyContent.body2
                                )
                        ),
                        GdsContentText.GdsContentTextString(
                            text =
                                intArrayOf(
                                    bodyContent.body3
                                )
                        )
                    ),
                contentInternalPadding = PaddingValues(bottom = smallPadding),
                primaryButtonText = buttonText,
                onPrimary = goToSignIn
            )
    )
}

internal data class AuthScreenBodyContent(
    @StringRes val body1: Int,
    @StringRes val body2: Int,
    @StringRes val body3: Int
)

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun AuthErrorScreenPreview() {
    GdsTheme {
        AuthErrorBody(true)
    }
}

@ExcludeFromJacocoGeneratedReport
@ScreenPreview
@Composable
internal fun AuthErrorScreenNoWalletPreview() {
    GdsTheme {
        AuthErrorBody(false)
    }
}
