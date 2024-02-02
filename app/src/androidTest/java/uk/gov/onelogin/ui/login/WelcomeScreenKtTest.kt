package uk.gov.onelogin.ui.login

import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import uk.gov.android.authentication.LoginSession
import uk.gov.android.authentication.LoginSessionConfiguration
import uk.gov.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.ext.setupComposeTestRule
import uk.gov.onelogin.login.WelcomeScreen

@HiltAndroidTest
class WelcomeScreenKtTest : TestCase() {
    private val loginSession: LoginSession = mock()

    @Before
    fun setupNavigation() {
        composeTestRule.setupComposeTestRule { _ ->
            WelcomeScreen(loginSession = loginSession)
        }
    }

    private val signInTitle = hasText(resources.getString(R.string.signInTitle))
    private val signInSubTitle = hasText(resources.getString(R.string.signInSubTitle))
    private val signInButton = hasText(resources.getString(R.string.signInButton))

    @Test
    fun verifyStrings() {
        composeTestRule.onNode(signInTitle).assertIsDisplayed()
        composeTestRule.onNode(signInSubTitle).assertIsDisplayed()
        composeTestRule.onNode(signInButton).assertIsDisplayed()
    }

    @Test
    fun opensWebLoginViaCustomTab() {
        composeTestRule.onNode(signInButton).performClick()
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
        val loginSessionConfig = LoginSessionConfiguration(
            authorizeEndpoint = authorizeEndpoint,
            clientId = clientId,
            redirectUri = redirectUri,
            scopes = listOf(LoginSessionConfiguration.Scope.OPENID),
            tokenEndpoint = tokenEndpoint
        )

        verify(loginSession).present(
            any(),
            eq(loginSessionConfig)
        )
    }
}
