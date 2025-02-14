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
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.core.analytics.AnalyticsModule
import uk.gov.onelogin.mainnav.MainNavRoutes
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.navigation.NavigatorModule

@HiltAndroidTest
@UninstallModules(NavigatorModule::class, AnalyticsModule::class)
class BioOptInScreenTest : TestCase() {
    @BindValue
    var mockNavigator: Navigator = mock()

    @BindValue
    val analytics: AnalyticsLogger = mock()

    private val title = hasText(resources.getString(R.string.app_enableBiometricsTitle))
    private val content1 = hasText(resources.getString(R.string.app_enableBiometricsBody1))
    private val content2 = hasText(resources.getString(R.string.app_enableBiometricsBody2))
    private val content3 = hasText(resources.getString(R.string.app_enableBiometricsBody3))
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
        composeTestRule.onNode(content3).assertIsDisplayed()
        composeTestRule.onNode(primaryButton).assertIsDisplayed()
        composeTestRule.onNode(secondaryButton).assertIsDisplayed()
        verify(analytics).logEventV3Dot1(BioOptInAnalyticsViewModel.makeScreenEvent(context))
    }

    @Test
    fun testPrimaryButton() {
        composeTestRule.setContent {
            BiometricsOptInScreen()
        }
        composeTestRule.onNode(primaryButton).performClick()

        verify(mockNavigator).navigate(MainNavRoutes.Start, true)
        verify(analytics).logEventV3Dot1(
            BioOptInAnalyticsViewModel.makeButtonEvent(
                context,
                R.string.app_enableBiometricsButton
            )
        )
    }

    @Test
    fun testSecondaryButton() {
        composeTestRule.setContent {
            BiometricsOptInScreen()
        }
        composeTestRule.onNode(secondaryButton).performClick()

        verify(mockNavigator).navigate(MainNavRoutes.Start, true)
        verify(analytics).logEventV3Dot1(
            BioOptInAnalyticsViewModel.makeButtonEvent(
                context,
                R.string.app_enablePasscodeOrPatternButton
            )
        )
    }

    @Test
    fun previewTest() {
        composeTestRule.setContent {
            BiometricsPreview()
        }
    }
}
