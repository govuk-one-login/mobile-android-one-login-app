package uk.gov.onelogin.network.auth

import org.junit.Test
import uk.gov.onelogin.network.auth.TokenExchange.Companion.TokenExchangeCodeArgError

class TokenExchangeTest {
    @Test(expected=TokenExchangeCodeArgError::class)
    fun `throws an InvalidArgumentException when an empty code is passed`() {
        TokenExchange("")
    }
}
