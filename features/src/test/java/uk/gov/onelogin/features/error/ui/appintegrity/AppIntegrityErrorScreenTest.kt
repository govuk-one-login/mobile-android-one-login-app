package uk.gov.onelogin.features.error.ui.appintegrity

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class AppIntegrityErrorScreenTest : FragmentActivityTestCase() {
    private lateinit var analytics: AnalyticsLogger

    private lateinit var analyticsViewModel: AppIntegrityErrorAnalyticsViewModel

    private lateinit var viewModel: AppIntegrityErrorViewModel

    private val mockNavigator: Navigator = Mockito.mock()
    private var goBack = false

    private val errorTitle = hasText(resources.getString(R.string.app_appIntegrityErrorTitle))
    private val errorBody1 = hasText(resources.getString(R.string.app_appIntegrityErrorBody1))
    private val errorBody2 = hasText(resources.getString(R.string.app_appIntegrityErrorBody2))

    @Before
    fun setUp() {
        analytics = mock()
        goBack = false
        analyticsViewModel = AppIntegrityErrorAnalyticsViewModel(context, analytics)
        viewModel = AppIntegrityErrorViewModel(mockNavigator)
    }

    @Test
    fun appIntegrityErrorScreen() {
        composeTestRule.setContent {
            AppIntegrityErrorScreen(
                analyticsViewModel = analyticsViewModel,
                viewModel = viewModel
            )
            goBack = true
        }
        composeTestRule.onNode(errorTitle).assertIsDisplayed()
        composeTestRule.onNode(errorBody1).assertIsDisplayed()
        composeTestRule.onNode(errorBody2).assertIsDisplayed()

        assert(goBack)
        verify(analytics).logEventV3Dot1(
            AppIntegrityErrorAnalyticsViewModel.makeScreenEvent(context)
        )
    }

    @Test
    fun onBackClicked() {
        composeTestRule.setContent {
            AppIntegrityErrorScreen(
                analyticsViewModel = analyticsViewModel,
                viewModel = viewModel
            )
        }

        Espresso.pressBack()
        verify(analytics).logEventV3Dot1(AppIntegrityErrorAnalyticsViewModel.makeBackEvent(context))
    }

    @Test
    fun preview() {
        composeTestRule.setContent {
            AppIntegrityErrorScreenPreview()
        }
        composeTestRule.onNode(errorTitle).assertIsDisplayed()
        composeTestRule.onNode(errorBody1).assertIsDisplayed()
        composeTestRule.onNode(errorBody2).assertIsDisplayed()
    }
}
