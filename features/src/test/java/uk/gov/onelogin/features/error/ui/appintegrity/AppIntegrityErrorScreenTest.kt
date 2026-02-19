package uk.gov.onelogin.features.error.ui.appintegrity

import androidx.compose.ui.test.assertContentDescriptionContains
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.features.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class AppIntegrityErrorScreenTest : FragmentActivityTestCase() {
    private lateinit var analytics: AnalyticsLogger
    private lateinit var analyticsViewModel: AppIntegrityErrorAnalyticsViewModel
    private var goBack = false

    private val errorIconDescription = resources.getString(uk.gov.android.ui.patterns.R.string.error_icon_description)
    private val errorTitle = hasText(resources.getString(R.string.app_appIntegrityErrorTitle))
    private val errorBody1 = hasText(resources.getString(R.string.app_appIntegrityErrorBody1))
    private val errorBody2 = hasText(resources.getString(R.string.app_appIntegrityErrorBody2))

    @Before
    fun setUp() {
        analytics = mock()
        analyticsViewModel = AppIntegrityErrorAnalyticsViewModel(context, analytics)
        goBack = false
    }

    @Test
    fun appIntegrityErrorScreen() {
        composeTestRule.setContent {
            AppIntegrityErrorScreen(
                analyticsViewModel = analyticsViewModel
            )
        }
        composeTestRule
            .onNodeWithContentDescription(
                errorIconDescription
            ).assertContentDescriptionContains(errorIconDescription)
        composeTestRule.onNode(errorTitle).assertIsDisplayed()
        composeTestRule.onNode(errorBody1).assertIsDisplayed()
        composeTestRule.onNode(errorBody2).assertIsDisplayed()

        assert(!goBack)
    }

    @Test
    fun preview() {
        composeTestRule.setContent {
            AppIntegrityErrorScreenPreview()
        }
        composeTestRule
            .onNodeWithContentDescription(
                errorIconDescription
            ).assertContentDescriptionContains(errorIconDescription)
        composeTestRule.onNode(errorTitle).assertIsDisplayed()
        composeTestRule.onNode(errorBody1).assertIsDisplayed()
        composeTestRule.onNode(errorBody2).assertIsDisplayed()
    }
}
