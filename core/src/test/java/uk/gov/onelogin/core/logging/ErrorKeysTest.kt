package uk.gov.onelogin.core.logging

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import uk.gov.logging.api.v3.customkey.CustomKey
import uk.gov.onelogin.core.logging.ErrorKeys.actionKey
import uk.gov.onelogin.core.logging.ErrorKeys.componentKey

class ErrorKeysTest {
    @Test
    fun `componentKey creates key with app prefix`() {
        val result = componentKey("login")

        assertEquals(CustomKey.StringKey("component", "app.login"), result)
    }

    @Test
    fun `componentKey supports dotted components`() {
        val result = componentKey("wallet.store")

        assertEquals(CustomKey.StringKey("component", "app.wallet.store"), result)
    }

    @Test
    fun `componentKey supports snake case components`() {
        val result = componentKey("id_check")

        assertEquals(CustomKey.StringKey("component", "app.id_check"), result)
    }

    @Test
    fun `componentKey rejects uppercase`() {
        assertThrows<AssertionError> {
            componentKey("Login")
        }
    }

    @Test
    fun `componentKey rejects app prefix`() {
        assertThrows<AssertionError> {
            componentKey("app.login")
        }
    }

    @Test
    fun `actionKey creates action key`() {
        val result = actionKey("Redirect to app")

        assertEquals(CustomKey.StringKey("action", "Redirect to app"), result)
    }
}
