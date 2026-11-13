package uk.gov.onelogin.libraries.mockcriorchestratorsdk

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MockCriOrchestratorScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `displays initial mock cri orchestrator content`() {
        composeTestRule.setContent {
            MockCriOrchestratorContent()
        }

        composeTestRule
            .onNodeWithText("Mock ID Check (Test Build)")
            .assertIsDisplayed()
    }
}
