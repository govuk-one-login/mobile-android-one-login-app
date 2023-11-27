package uk.gov.onelogin.login

import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.uiautomator.UiSelector
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import uk.gov.onelogin.MainActivity
import uk.gov.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.ext.setupComposeTestRule
import uk.gov.onelogin.login.nonce.INonceGenerator
import uk.gov.onelogin.login.nonce.NonceGeneratorModule
import uk.gov.onelogin.login.nonce.NonceGeneratorStub
import uk.gov.onelogin.login.state.IStateGenerator
import uk.gov.onelogin.login.state.StateGeneratorModule
import uk.gov.onelogin.login.state.StateGeneratorStub
import uk.gov.onelogin.test.settings.SettingsController
import java.util.UUID

@HiltAndroidTest
@UninstallModules(
    NonceGeneratorModule::class,
    StateGeneratorModule::class
)
class SuccessfulLoginTest : TestCase() {
    private var scenario: ActivityScenario<MainActivity>? = null

    private val nonce = UUID.randomUUID().toString()

    @BindValue
    val nonceGenerator: INonceGenerator = NonceGeneratorStub(
        nonce = nonce
    )

    private val state = UUID.randomUUID().toString()

    @BindValue
    val stateGenerator: IStateGenerator = StateGeneratorStub(
        state = state
    )

    @Before
    fun setup() {
        enableOpenByDefault()
        setupNavigation()
        initializeIntents()
    }

    private fun initializeIntents() {
        Intents.init()
    }

    private fun enableOpenByDefault() {
        SettingsController(
            context = context,
            device = device
        ).enableOpenLinksByDefault()
    }

    private fun setupNavigation() {
        navController = composeTestRule.setupComposeTestRule { _ ->
            LoadingScreen()
        }
    }

    @After
    fun tearDown() {
        closeScenario()
        releaseIntents()
    }

    private fun closeScenario() {
        scenario?.close()
    }

    private fun releaseIntents() {
        Intents.release()
    }

    @Test
    fun logsIntoTheAppAndExchangesAuthCodeForTokens() {
        scenario = launchActivity()
        device.waitForIdle(1000)
        composeTestRule.onNode(
            hasText(
                resources.getString(R.string.signInButton)
            )
        ).apply {
            assertIsDisplayed()
            performClick()
        }

        Intents.intended(
            CoreMatchers.allOf(
                IntentMatchers.hasData(loginIntentData())
            )
        )

        device.waitForIdle()
        device.findObject(UiSelector().text("Login"))?.let {
            it.click()
        } ?: fail("Could not find login button of auth stub")
        device.waitForIdle()
    }

    private fun loginIntentData(): Uri {
        val baseUri = resources.getString(
            R.string.openIdConnectBaseUrl,
            resources.getString(R.string.openIdConnectAuthorizeEndpoint)
        )
        val redirectUri = resources.getString(
            R.string.webBaseUrl,
            resources.getString(R.string.webRedirectEndpoint)
        )
        val clientID = resources.getString(R.string.openIdConnectClientId)

        return UriBuilder(
            state = state,
            nonce = nonce,
            baseUri = baseUri,
            redirectUri = redirectUri,
            clientID = clientID
        ).url
    }
}
