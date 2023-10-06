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

class WelcomeScreenKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val state = UUID.randomUUID().toString()
    private val nonce = UUID.randomUUID().toString()

    private lateinit var navController: TestNavHostController

    @Before
    fun setupNavigation() {
        Intents.init()
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            WelcomeScreen(state = state)
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
                hasData(
                    Uri.parse("https://oidc.staging.account.gov.uk/authorize")
                        .buildUpon().appendQueryParameter("response_type", "code")
                        .appendQueryParameter("scope", "openid email phone offline_access")
                        .appendQueryParameter("client_id", "CLIENT_ID")
                        .appendQueryParameter("state", state)
                        .appendQueryParameter(
                            "redirect_uri",
                            "https://mobile-staging.account.gov.uk/redirect",
                        )
                        .appendQueryParameter("nonce", nonce)
                        .appendQueryParameter("vtr", "[\"Cl.Cm.P0\"]")
                        .appendQueryParameter("ui_locales", "en")
                        .build(),
                ),
            ),
        )
    }
}
