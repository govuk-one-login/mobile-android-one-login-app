package uk.gov.onelogin.login

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import uk.gov.android.authentication.ILoginSession
import uk.gov.android.authentication.LoginSessionConfiguration
import uk.gov.android.ui.components.content.GdsContentText
import uk.gov.android.ui.pages.LandingPage
import uk.gov.android.ui.pages.LandingPageParameters
import uk.gov.onelogin.R

@Composable
fun WelcomeScreen(
    context: Context = LocalContext.current,
    loginSession: ILoginSession,
) {
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
                        R.string.openIdConnectBaseUrl,
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
                    .init(context = context)
                    .present(
                        configuration = LoginSessionConfiguration(
                            authorizeEndpoint = authorizeEndpoint,
                            clientId = clientId,
                            redirectUri = redirectUri,
                            scopes = "openid",
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

internal class WelcomeScreenParameters : PreviewParameterProvider<UriBuilder> {
    override val values: Sequence<UriBuilder> = sequenceOf(
        UriBuilder(
            baseUri = "",
            clientID = "",
            nonce = "",
            redirectUri = "",
            state = ""
        )
    )
}

//@Preview(
//    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_NO
//)
//@Preview(
//    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_YES
//)
//@Composable
//private fun Preview(
//    @PreviewParameter(WelcomeScreenParameters::class)
//    builder: UriBuilder
//) {
//    GdsTheme {
//        WelcomeScreen(
//            builder = builder
//        )
//    }
//}
