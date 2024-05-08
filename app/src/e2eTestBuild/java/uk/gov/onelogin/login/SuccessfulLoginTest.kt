package uk.gov.onelogin.login

import android.content.Intent
import android.net.Uri
import android.view.KeyEvent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.espresso.intent.Intents
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.ext.setupComposeTestRule
import uk.gov.onelogin.login.nonce.INonceGenerator
import uk.gov.onelogin.login.nonce.NonceGeneratorModule
import uk.gov.onelogin.login.nonce.NonceGeneratorStub
import uk.gov.onelogin.login.ui.LoadingScreen
import uk.gov.onelogin.matchers.IsUUID
import uk.gov.onelogin.matchers.MatchesUri
import uk.gov.onelogin.test.settings.SettingsController
import java.util.UUID

@HiltAndroidTest
@UninstallModules(
    NonceGeneratorModule::class
)
class SuccessfulLoginTest : TestCase() {
    private val nonce = UUID.randomUUID().toString()

    @BindValue
    val nonceGenerator: INonceGenerator = NonceGeneratorStub(
        nonce = nonce
    )

    private val state = UUID.randomUUID().toString()

    @Before
    fun setup() {
        initializeIntents()
        enableOpenByDefault()
        setupNavigation()
    }

    @After
    fun tearDown() {
        releaseIntents()
    }

    private fun initializeIntents() {
        Intents.init()
    }

    private fun releaseIntents() {
        Intents.release()
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

    @Ignore("This test is failing on the pipeline emulator, we're struggling to figure out why")
    @Test
    fun logsIntoTheAppAndExchangesAuthCodeForTokens() {
        device.pressHome()
        val intent = context.packageManager.getLaunchIntentForPackage(
            context.packageName
        )
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        val signInSelector = resources.getString(R.string.signInButton)
        device.wait(
            Until.findObject(By.text(signInSelector)), WAIT_FOR_OBJECT_TIMEOUT
        )
        composeTestRule.onNode(hasText(signInSelector)).apply {
            assertIsDisplayed()
            performClick()
        }

        device.apply {
            device.wait(
                Until.findObject(By.text("Accept & continue")), WAIT_FOR_OBJECT_TIMEOUT
            )?.let {
                it.click()
                device.wait(
                    Until.findObject(By.text("No thanks")), WAIT_FOR_OBJECT_TIMEOUT
                )?.click()
            }


            val loginSelector = By.text("Login")
            wait(
                Until.findObject(loginSelector),
                WAIT_FOR_OBJECT_TIMEOUT
            )
            pressKeyCode(KeyEvent.KEYCODE_TAB)
            pressKeyCode(KeyEvent.KEYCODE_TAB)
            pressKeyCode(KeyEvent.KEYCODE_TAB)
            pressKeyCode(KeyEvent.KEYCODE_ENTER)
            waitForIdle()
            wait(
                Until.findObject(
                    By.text(
                        resources.getString(R.string.signInButton)
                    )
                ),
                WAIT_FOR_OBJECT_TIMEOUT
            )
        }

        val authorizeUrl = Uri.parse(
            resources.getString(
                R.string.openIdConnectBaseUrl,
                resources.getString(R.string.openIdConnectAuthorizeEndpoint)
            )
        )
        val redirectUrl = Uri.parse(
            resources.getString(
                R.string.webBaseUrl,
                resources.getString(R.string.webRedirectEndpoint)
            )
        )

        Intents.intended(
            MatchesUri(
                host = authorizeUrl.host,
                path = authorizeUrl.path,
                parameters = mapOf(
                    "client_id" to IsEqual(
                        resources.getString(R.string.openIdConnectClientId)
                    ),
                    "nonce" to IsUUID(),
                    "redirect_uri" to IsEqual(
                        resources.getString(
                            R.string.webBaseUrl,
                            resources.getString(R.string.webRedirectEndpoint)
                        )
                    ),
                    "response_type" to IsEqual("code"),
                    "scope" to IsEqual("openid"),
                    "ui_locales" to IsEqual("en"),
                    "vtr" to IsEqual("[\"Cl.Cm.P0\"]")
                )
            )
        )

        device.wait(
            Until.findObject(
                By.text(
                    resources.getString(R.string.homeScreenTitle)
                )
            ),
            WAIT_FOR_OBJECT_TIMEOUT
        )

        Intents.intended(
            MatchesUri(
                host = redirectUrl.host,
                path = redirectUrl.path,
                parameters = mapOf(
                    "code" to IsUUID()
                )
            )
        )
        device.waitForIdle()
        composeTestRule.onNode(hasText("Access Token")).apply {
            performScrollTo()
            assertIsDisplayed()
        }
        composeTestRule.onNode(hasTestTag("homeScreen-accessToken")).apply {
            performScrollTo()
            assertIsDisplayed()
        }
        composeTestRule.onNode(hasText("ID Token")).apply {
            performScrollTo()
            assertIsDisplayed()
        }
        composeTestRule.onNode(hasTestTag("homeScreen-idToken")).apply {
            performScrollTo()
            assertIsDisplayed()
        }
        composeTestRule.onNode(hasText("Refresh Token")).apply {
            performScrollTo()
            assertIsDisplayed()
        }
        composeTestRule.onNode(hasTestTag("homeScreen-refreshToken")).apply {
            performScrollTo()
            assertIsDisplayed()
        }
    }

    companion object {
        const val WAIT_FOR_OBJECT_TIMEOUT = 5000L
    }
}
