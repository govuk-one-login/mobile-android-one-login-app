package uk.gov.onelogin.signOut.ui

import androidx.compose.runtime.Composable
import uk.gov.android.onelogin.R
import uk.gov.android.ui.components.content.GdsContentText
import uk.gov.android.ui.pages.LandingPage
import uk.gov.android.ui.pages.LandingPageParameters
import uk.gov.android.ui.theme.smallPadding

@Composable
fun SignedOutInfoScreen(
    signIn: () -> Unit
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
            primaryButtonText = R.string.app_SignInWithGovUKOneLoginButton,
            onPrimary = signIn
        )
    )
}
