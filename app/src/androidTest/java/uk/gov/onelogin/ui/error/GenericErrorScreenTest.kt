package uk.gov.onelogin.ui.error

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase

@HiltAndroidTest
class GenericErrorScreenTest : TestCase() {

    private var primaryClicked = false

    private val errorTitle = hasText(resources.getString(R.string.app_somethingWentWrongErrorTitle))
    private val errorBody = hasText(resources.getString(R.string.app_somethingWentWrongErrorBody))
    private val primaryButton = hasText(resources.getString(R.string.app_closeButton))

    @Before
    fun setUp() {
        primaryClicked = false
        composeTestRule.setContent {
            GenericErrorScreen(onClick = { primaryClicked = true })
        }
    }

    @Test
    fun genericErrorScreen() {
        composeTestRule.onNode(errorTitle).assertIsDisplayed()
        composeTestRule.onNode(errorBody).assertIsDisplayed()
        composeTestRule.onNode(primaryButton).assertIsDisplayed()
    }

    @Test
    fun onPrimaryClicked() {
        composeTestRule.onNode(primaryButton).performClick()
        assert(primaryClicked)
    }
}
