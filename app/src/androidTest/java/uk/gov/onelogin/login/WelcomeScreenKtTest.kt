package uk.gov.onelogin.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.ext.setupComposeTestRule
import java.util.UUID

@HiltAndroidTest
class WelcomeScreenKtTest : TestCase() {
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
        clientID = clientID
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

        Intents.intended(
            allOf(
                hasData(builder.url)
            )
        )
    }
}
