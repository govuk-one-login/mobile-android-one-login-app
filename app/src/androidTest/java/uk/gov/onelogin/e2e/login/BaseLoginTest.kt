package uk.gov.onelogin.e2e.login

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.uiautomator.By
import org.junit.Before
import org.junit.Rule
import uk.gov.onelogin.MainActivity
import uk.gov.onelogin.e2e.controller.SettingsController
import uk.gov.onelogin.e2e.controller.TestCase
import uk.gov.onelogin.e2e.selectors.BySelectors.loginButton

open class BaseLoginTest : TestCase() {
    private val settingsController = SettingsController(
        context,
        phoneController
    )

    @JvmField
    @Rule(order = 3)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
        settingsController.enableOpenLinksByDefault()
    }

    protected fun startApp() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    protected fun goodLogin() {
        phoneController.apply {
            waitUntilIdle(WAIT_FOR_OBJECT_TIMEOUT)
            click(
                WAIT_FOR_OBJECT_TIMEOUT,
                loginButton(context) to "Press login button"
            )
            waitUntilIdle(WAIT_FOR_OBJECT_TIMEOUT)

            optionalClick(
                2000L,
                By.text("Accept & continue") to "accept chrome",
                By.text("No thanks") to "decline chrome"
            )

            click(
                WAIT_FOR_OBJECT_TIMEOUT,
                By.text("Login") to "Press web login button"
            )

            phoneController.waitUntilIdle(10000L)
        }
    }

    companion object {
        const val WAIT_FOR_OBJECT_TIMEOUT = 60_000L
    }
}
