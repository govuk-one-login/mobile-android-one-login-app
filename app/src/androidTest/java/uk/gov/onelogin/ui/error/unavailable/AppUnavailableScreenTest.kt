package uk.gov.onelogin.ui.error.unavailable

import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.core.analytics.AnalyticsModule

@HiltAndroidTest
@UninstallModules(
    AnalyticsModule::class
)
class AppUnavailableScreenTest : TestCase() {

    @BindValue
    val analytics: AnalyticsLogger = mock()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun screenViewAnalyticsLogOnResume() {
        composeTestRule.setContent {
            AppUnavailableScreen()
        }
        val event = UnavailableAnalyticsViewModel.makeUnavailableViewEvent(context)

        verify(analytics).logEventV3Dot1(event)
    }

    @Test
    fun onHardwareBackButton() {
        composeTestRule.setContent {
            AppUnavailableScreen()
        }
        val event = UnavailableAnalyticsViewModel.makeBackEvent(context)
        composeTestRule.waitForIdle()
        try {
            Espresso.pressBack()
        } catch (_: NoActivityResumedException) {
        }

        verify(analytics).logEventV3Dot1(event)
    }

    @Test
    fun previewTest() {
        composeTestRule.setContent {
            AppUnavailablePreview()
        }
    }
}
