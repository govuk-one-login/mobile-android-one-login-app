package uk.gov.onelogin.login.ui.biooptin

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.mainnav.MainNavRoutes
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.navigation.NavigatorModule

@HiltAndroidTest
@UninstallModules(NavigatorModule::class)
class BioOptInScreenTest : TestCase() {
    @BindValue
    var mockNavigator: Navigator = mock()

    private val title = hasText(resources.getString(R.string.app_enableBiometricsTitle))
    private val content1 = hasText(resources.getString(R.string.app_enableBiometricsBody1))
    private val content2 = hasText(resources.getString(R.string.app_enableBiometricsBody2))
    private val footnote = hasText(resources.getString(R.string.app_enableBiometricsFootnote))
    private val primaryButton = hasText(resources.getString(R.string.app_enableBiometricsButton))
    private val secondaryButton =
        hasText(resources.getString(R.string.app_enablePasscodeOrPatternButton))

    @Before
    fun setupNavigation() {
        hiltRule.inject()
    }

    @Test
    fun verifyStrings() {
        composeTestRule.setContent {
            BiometricsOptInScreen()
        }
        composeTestRule.onNode(title).assertIsDisplayed()
        composeTestRule.onNode(content1).assertIsDisplayed()
        composeTestRule.onNode(content2).assertIsDisplayed()
        composeTestRule.onNode(footnote).assertIsDisplayed()
        composeTestRule.onNode(primaryButton).assertIsDisplayed()
        composeTestRule.onNode(secondaryButton).assertIsDisplayed()
    }

    @Test
    fun testPrimaryButton() {
        composeTestRule.setContent {
            BiometricsOptInScreen()
        }
        composeTestRule.onNode(primaryButton).performClick()

        verify(mockNavigator).navigate(MainNavRoutes.Start, true)
    }

    @Test
    fun testSecondaryButton() {
        composeTestRule.setContent {
            BiometricsOptInScreen()
        }
        composeTestRule.onNode(secondaryButton).performClick()

        verify(mockNavigator).navigate(MainNavRoutes.Start, true)
    }

    @Test
    fun previewTest() {
        composeTestRule.setContent {
            BiometricsPreview()
        }
    }
}
