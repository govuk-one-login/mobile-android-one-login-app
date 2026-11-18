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
import uk.gov.onelogin.libraries.mocknetwork.TestMockNetworkScenarios
import uk.gov.onelogin.libraries.mocknetwork.ui.MockNetworkScreen
import uk.gov.onelogin.libraries.mocknetwork.ui.MockNetworkViewModel

@RunWith(AndroidJUnit4::class)
class MockNetworkScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `renders a button per scenario`() {
        composeTestRule.setContent {
            MockNetworkScreen(
                viewModel = MockNetworkViewModel(MockNetworkConfig(TestMockNetworkScenarios.all)),
            )
        }

        TestMockNetworkScenarios.all.forEach { scenario ->
            composeTestRule
                .onNodeWithTag("mockNetworkScenario_${scenario.name}")
                .assertIsDisplayed()
        }
    }

    @Test
    fun `selecting a scenario updates the config`() {
        val config = MockNetworkConfig(TestMockNetworkScenarios.all)

        composeTestRule.setContent {
            MockNetworkScreen(viewModel = MockNetworkViewModel(config))
        }

        composeTestRule
            .onNodeWithTag("mockNetworkScenario_${TestMockNetworkScenarios.serverError.name}")
            .performClick()

        composeTestRule.waitForIdle()
        assertEquals(TestMockNetworkScenarios.serverError, config.scenario.value)
    }
}
