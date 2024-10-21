package uk.gov.onelogin.coverage

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class EmptyScreen1Test {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun fakeTestForCoverage() {
        composeTestRule.setContent {
            EmptyScreen1()
        }
    }

    @Test
    fun fakeTestForPreviewCoverage() {
        composeTestRule.setContent {
            Empty1Preview()
        }
    }
}
