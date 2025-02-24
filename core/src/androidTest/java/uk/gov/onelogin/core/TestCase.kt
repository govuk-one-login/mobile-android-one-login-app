package uk.gov.onelogin.core

import android.content.Context
import android.content.res.Resources
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Rule

abstract class TestCase {
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    protected val context: Context = ApplicationProvider.getApplicationContext()

    protected val resources: Resources = context.resources

    protected val device: UiDevice =
        UiDevice.getInstance(
            InstrumentationRegistry.getInstrumentation()
        )
}
