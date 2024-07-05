package uk.gov.onelogin.e2e.controller

import android.content.Context
import android.content.res.Resources
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule

open class TestCase {
    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var testNameRule: TestCaseNameWatcher = TestCaseNameWatcher()

    protected var screenshotName: String? = null

    protected open val phoneController =
        PhoneController(testNameRule = testNameRule)

    protected val context: Context = ApplicationProvider.getApplicationContext()

    protected val resources: Resources = context.resources

    @Before
    fun setScreenshotName() {
        screenshotName = "${testNameRule.methodName}_${resources.configuration.locales.get(0)}"
    }
}
