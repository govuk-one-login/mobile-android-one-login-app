package uk.gov.onelogin

import android.content.Context
import android.content.res.Resources
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Rule

abstract class TestCase {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    var navController: TestNavHostController? = null

    protected val context: Context = ApplicationProvider.getApplicationContext()

    protected val resources: Resources = context.resources

    protected val device: UiDevice = UiDevice.getInstance(
        InstrumentationRegistry.getInstrumentation()
    )
}
