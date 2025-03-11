package uk.gov.onelogin.features.error.ui.generic

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.features.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class GenericErrorScreenTest : FragmentActivityTestCase() {
    private lateinit var analyticsLogger: AnalyticsLogger
    private lateinit var viewModel: GenericErrorAnalyticsViewModel
    private var primaryClicked = false

    private val errorTitle = hasText(resources.getString(R.string.app_somethingWentWrongErrorTitle))
    private val errorBody = hasText(resources.getString(R.string.app_somethingWentWrongErrorBody))
    private val primaryButton = hasText(resources.getString(R.string.app_closeButton))

    @Before
    fun setUp() {
        analyticsLogger = mock()
        viewModel = GenericErrorAnalyticsViewModel(context, analyticsLogger)
        primaryClicked = false
    }

    @Test
    fun genericErrorScreen() {
        composeTestRule.setContent {
            GenericErrorScreen(
                analyticsViewModel = viewModel,
                onClick = { primaryClicked = true }
            )
        }

        composeTestRule.onNode(errorTitle).assertIsDisplayed()
        composeTestRule.onNode(errorBody).assertIsDisplayed()
        composeTestRule.onNode(primaryButton).apply {
            assertIsDisplayed()
            performClick()
        }

        assert(primaryClicked)
    }

    @Test
    fun onBackClicked() {
        composeTestRule.setContent {
            GenericErrorScreen(
                analyticsViewModel = viewModel,
                onClick = { primaryClicked = true }
            )
        }

        Espresso.pressBack()
    }

    @Test
    fun preview() {
        composeTestRule.setContent {
            GenericErrorPreview()
        }
    }
}
