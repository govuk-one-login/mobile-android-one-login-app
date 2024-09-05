package uk.gov.onelogin.optin.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class DisallowedStateChangeTest {
    @Test
    fun `default construction`() {
        // Given the default construction of DisallowedStateChange
        val exception = DisallowedStateChange()
        // Then the state value is AnalyticsOptInState.None and the message is as follows
        assertEquals(expected = "The following state (None) cannot be changed to", actual = exception.message)
        assertEquals(expected = AnalyticsOptInState.None, actual = exception.state)
    }

    @Test
    fun `non-default construction`() {
        // Given the non-default construction of DisallowedStateChange
        val exception = DisallowedStateChange(AnalyticsOptInState.Yes)
        // Then the state value is the one passed in and the message is as follows
        assertEquals(expected = "The following state (Yes) cannot be changed to", actual = exception.message)
        assertEquals(expected = AnalyticsOptInState.Yes, actual = exception.state)
    }
}
