package uk.gov.onelogin.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.login.ui.BiometricsOptInScreen

@HiltAndroidTest
class BioOptInScreenKtTest : TestCase() {
    private var primaryClicked: Int = 0
    private var secondaryClicked: Int = 0

    @Before
    fun setupNavigation() {
        primaryClicked = 0
        secondaryClicked = 0

        hiltRule.inject()
        composeTestRule.setContent {
            BiometricsOptInScreen(
                onPrimary = { primaryClicked++ },
                onSecondary = { secondaryClicked++ }
            )
        }
    }

    private val title = hasText(resources.getString(R.string.app_enableBiometricsTitle))
    private val content1 = hasText(resources.getString(R.string.app_enableBiometricsBody1))
    private val content2 = hasText(resources.getString(R.string.app_enableBiometricsBody2))
    private val footnote = hasText(resources.getString(R.string.app_enableBiometricsFootnote))
    private val primaryButton = hasText(resources.getString(R.string.app_enableBiometricsButton))
    private val secondaryButton =
        hasText(resources.getString(R.string.app_enablePasscodeOrPatternButton))

    @Test
    fun verifyStrings() {
        composeTestRule.onNode(title).assertIsDisplayed()
        composeTestRule.onNode(content1).assertIsDisplayed()
        composeTestRule.onNode(content2).assertIsDisplayed()
        composeTestRule.onNode(footnote).assertIsDisplayed()
        composeTestRule.onNode(primaryButton).assertIsDisplayed()
        composeTestRule.onNode(secondaryButton).assertIsDisplayed()
    }

    @Test
    fun testPrimaryButton() {
        composeTestRule.onNode(primaryButton).performClick()

        assertEquals(1, primaryClicked)
    }

    @Test
    fun testSecondaryButton() {
        composeTestRule.onNode(secondaryButton).performClick()

        assertEquals(1, secondaryClicked)
    }
}
