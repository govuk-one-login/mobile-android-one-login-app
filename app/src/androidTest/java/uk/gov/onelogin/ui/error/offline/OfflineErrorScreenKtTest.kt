package uk.gov.onelogin.ui.error.offline

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase

@HiltAndroidTest
class OfflineErrorScreenKtTest : TestCase() {

    private var retryClicked = false

    private val errorTitle = hasText(resources.getString(R.string.app_networkErrorTitle))
    private val errorBody = hasText(resources.getString(R.string.app_networkErrorBody))
    private val tryAgainButton = hasText(resources.getString(R.string.app_tryAgainButton))

    @Before
    fun setUp() {
        retryClicked = false
        composeTestRule.setContent {
            OfflineErrorScreen(onRetryClick = { retryClicked = true })
        }
    }

    @Test
    fun offlineErrorScreen() {
        composeTestRule.onNode(errorTitle).assertIsDisplayed()
        composeTestRule.onNode(errorBody).assertIsDisplayed()
        composeTestRule.onNode(tryAgainButton).assertIsDisplayed()
    }

    @Test
    fun tryAgain() {
        whenWeClickRetry()
        itPopsBackWithRetryRequest()
    }

    private fun whenWeClickRetry() {
        composeTestRule.onNode(tryAgainButton).performClick()
    }

    private fun itPopsBackWithRetryRequest() = assert(retryClicked)
}
