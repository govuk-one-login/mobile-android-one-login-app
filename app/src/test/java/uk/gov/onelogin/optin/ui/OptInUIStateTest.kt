package uk.gov.onelogin.optin.ui

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OptInUIStateTest {
    private lateinit var uiState: OptInUIState

    @Test
    fun `hasButtonsOn value for PreChoice is true`() {
        // Given the PreChoice OptInUIState
        uiState = OptInUIState.PreChoice
        // Then hasButtonsOn is true
        assertTrue(uiState.hasButtonsOn)
    }

    @Test
    fun `hasButtonsOn value for PostChoice is false`() {
        // Given the PostChoice OptInUIState
        uiState = OptInUIState.PostChoice
        // Then hasButtonsOn is false
        assertFalse(uiState.hasButtonsOn)
    }
}
