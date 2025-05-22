package uk.gov.onelogin.features.settings.ui.biomtericsoptin

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.settings.ui.biometricsoptin.BiometricsOptInScreen

@RunWith(AndroidJUnit4::class)
class BiometricsOptInScreenTest : FragmentActivityTestCase() {
    @Test
    fun testUI() {
        composeTestRule.setContent {
            BiometricsOptInScreen()
        }

        composeTestRule.onNodeWithText(
            "Placeholder to be able to test navigation",
            substring = true
        ).assertIsDisplayed()
    }
}
