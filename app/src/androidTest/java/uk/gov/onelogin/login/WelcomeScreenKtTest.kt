package uk.gov.onelogin.login

import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID
import uk.gov.onelogin.ext.setupComposeTestRule

class WelcomeScreenKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val state = UUID.randomUUID().toString()
    private val nonce = UUID.randomUUID().toString()
    private val baseUri = "https://oidc.staging.account.gov.uk/authorize"
    private val redirectUri = "https://mobile-staging.account.gov.uk/redirect"
    private val clientID = "CLIENT_ID"

    private val builder = UriBuilder(
        state = state,
        nonce = nonce,
        baseUri = baseUri,
        redirectUri = redirectUri,
        clientID = clientID,
    )

    @Before
    fun setupNavigation() {
        Intents.init()
        composeTestRule.setupComposeTestRule { _ ->
            WelcomeScreen(builder = builder)
        }
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    private val signInButton = hasText("Sign In")

    @Test
    fun verifyStrings() {
        composeTestRule.onNode(signInButton).assertIsDisplayed()
    }

    @Test
    fun opensWebLoginViaCustomTab() {
        composeTestRule.onNode(signInButton).performClick()

        Intents.intended(
            allOf(
                hasData(builder.url),
            ),
        )
    }
}
