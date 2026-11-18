package uk.gov.onelogin.libraries.mocknetwork.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkConfig
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkScenario
import uk.gov.onelogin.libraries.mocknetwork.ui.MockNetworkScreen
import uk.gov.onelogin.libraries.mocknetwork.ui.MockNetworkViewModel

@RunWith(AndroidJUnit4::class)
class MockNetworkScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `renders a button per scenario`() {
        composeTestRule.setContent {
            MockNetworkScreen(viewModel = MockNetworkViewModel(MockNetworkConfig()))
        }

        MockNetworkScenario.entries.forEach { scenario ->
            composeTestRule
                .onNodeWithTag("mockNetworkScenario_${scenario.name}")
                .assertIsDisplayed()
        }
    }

    @Test
    fun `selecting a scenario updates the config`() {
        val config = MockNetworkConfig(initialScenario = MockNetworkScenario.EMPTY_SUCCESS)

        composeTestRule.setContent {
            MockNetworkScreen(viewModel = MockNetworkViewModel(config))
        }

        composeTestRule
            .onNodeWithTag("mockNetworkScenario_${MockNetworkScenario.SERVER_ERROR.name}")
            .performClick()

        composeTestRule.waitForIdle()
        assertEquals(MockNetworkScenario.SERVER_ERROR, config.scenario.value)
    }
}
