package uk.gov.onelogin.login

import android.content.Context
import android.content.res.Configuration
import androidx.browser.customtabs.CustomTabsIntent.Builder
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import uk.gov.documentchecking.pages.LandingPage
import uk.gov.documentchecking.pages.LandingPageParameters
import uk.gov.onelogin.R
import uk.gov.ui.components.content.GdsContentText
import uk.gov.ui.theme.GdsTheme

@Composable
fun WelcomeScreen(
    builder: UriBuilder,
    context: Context = LocalContext.current,
) {
    LandingPage(landingPageParameters = LandingPageParameters(
        content = listOf(
            GdsContentText.GdsContentTextString(
                text = intArrayOf(
                    R.string.signInSubTitle
                )
            )
        ),
        onPrimary = {
            val intent = Builder()
                .build()
            intent.launchUrl(context, builder.url)
        },
        primaryButtonText = R.string.signInButton,
        title = R.string.signInTitle,
        topIcon = R.drawable.app_icon,
    ))
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

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun Preview(
    @PreviewParameter(WelcomeScreenParameters::class)
    builder: UriBuilder
) {
    GdsTheme {
        WelcomeScreen(
            builder = builder
        )
    }
}
