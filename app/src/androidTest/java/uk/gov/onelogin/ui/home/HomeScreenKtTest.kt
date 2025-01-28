package uk.gov.onelogin.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.ext.setupComposeTestRule

@HiltAndroidTest
class HomeScreenKtTest : TestCase() {
    @Test
    fun initialisesHomeScreen() {
        composeTestRule.setupComposeTestRule { _ ->
            HomeScreen()
        }
    }

    @Test
    fun testCriOrchestratorInit() {
        composeTestRule.setupComposeTestRule { _ ->
            HomeScreen()
        }
        composeTestRule.apply {
            onNodeWithText(
                "CRI Orchestrator Init:",
                substring = true
            ).assertIsDisplayed()

            onNodeWithText(
                "uk.gov.onelogin.criorchestrator.sdk",
                substring = true
            ).assertIsDisplayed()
        }
    }
}
