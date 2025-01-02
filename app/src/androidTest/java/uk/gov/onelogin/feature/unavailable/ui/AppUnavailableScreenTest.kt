package uk.gov.onelogin.feature.unavailable.ui

import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import uk.gov.onelogin.TestCase

@HiltAndroidTest
class AppUnavailableScreenTest : TestCase() {

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun onHardwareBackButton() {
        composeTestRule.setContent {
            AppUnavailableScreen()
        }
        composeTestRule.waitForIdle()
        try {
            Espresso.pressBack()
        } catch (_: NoActivityResumedException) {
        }
    }

    @Test
    fun previewTest() {
        composeTestRule.setContent {
            AppUnavailablePreview()
        }
    }
}
