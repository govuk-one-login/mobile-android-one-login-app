package uk.gov.onelogin.ui.error

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import uk.gov.onelogin.R
import uk.gov.onelogin.TestCase

@HiltAndroidTest
class OfflineErrorScreenKtTest : TestCase() {

    @BindValue
    val navHostController: NavHostController = Mockito.mock()

    private val errorTitle = hasText(resources.getString(R.string.app_networkErrorTitle))
    private val errorBody = hasText(resources.getString(R.string.app_networkErrorBody))
    private val tryAgainButton = hasText(resources.getString(R.string.app_tryAgainButton))

    @Before
    fun setUp() {
        composeTestRule.setContent {
            OfflineErrorScreen(navController = navHostController)
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

    private fun itPopsBackWithRetryRequest() {
        verify(navHostController).previousBackStackEntry?.savedStateHandle?.set(
            eq(OFFLINE_ERROR_TRY_AGAIN_KEY),
            eq(true)
        )
        verify(navHostController).popBackStack()
    }
}