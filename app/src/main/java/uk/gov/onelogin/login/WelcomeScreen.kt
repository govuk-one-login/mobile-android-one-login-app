package uk.gov.onelogin.login

import android.app.Activity
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.android.authentication.AppAuthSession
import uk.gov.android.authentication.LoginSession
import uk.gov.android.authentication.LoginSessionConfiguration
import uk.gov.android.features.FeatureFlags
import uk.gov.android.features.InMemoryFeatureFlags
import uk.gov.android.ui.components.content.GdsContentText
import uk.gov.android.ui.pages.LandingPage
import uk.gov.android.ui.pages.LandingPageParameters
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.R
import uk.gov.onelogin.features.StsFeatureFlag

@Composable
fun WelcomeScreen(
    loginSession: LoginSession,
    featureFlags: FeatureFlags
) {
    val context = LocalContext.current
    LandingPage(
        landingPageParameters = LandingPageParameters(
            content = listOf(
                GdsContentText.GdsContentTextString(
                    text = intArrayOf(
                        R.string.signInSubTitle
                    )
                )
            ),
            onPrimary = {
                val authorizeEndpoint = Uri.parse(
                    context.resources.getString(
                        if (featureFlags[StsFeatureFlag.STS_ENDPOINT]) {
                            R.string.stsUrl
                        } else {
                            R.string.openIdConnectBaseUrl
                        },
                        context.resources.getString(R.string.openIdConnectAuthorizeEndpoint)
                    )
                )
                val tokenEndpoint = Uri.parse(
                    context.resources.getString(
                        R.string.apiBaseUrl,
                        context.resources.getString(R.string.tokenExchangeEndpoint)
                    )
                )
                val redirectUri = Uri.parse(
                    context.resources.getString(
                        R.string.webBaseUrl,
                        context.resources.getString(R.string.webRedirectEndpoint)
                    )
                )
                val clientId = context.resources.getString(R.string.openIdConnectClientId)

                loginSession
                    .present(
                        context as Activity,
                        configuration = LoginSessionConfiguration(
                            authorizeEndpoint = authorizeEndpoint,
                            clientId = clientId,
                            redirectUri = redirectUri,
                            scopes = listOf(LoginSessionConfiguration.Scope.OPENID),
                            tokenEndpoint = tokenEndpoint
                        )
                    )
            },
            primaryButtonText = R.string.signInButton,
            title = R.string.signInTitle,
            topIcon = R.drawable.app_icon
        )
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun Preview() {
    GdsTheme {
        WelcomeScreen(
            AppAuthSession(LocalContext.current),
            InMemoryFeatureFlags(setOf())
        )
    }
}
