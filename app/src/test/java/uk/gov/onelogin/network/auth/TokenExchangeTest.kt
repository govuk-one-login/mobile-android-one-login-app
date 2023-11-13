package uk.gov.onelogin.network.auth

import org.junit.Before
import org.junit.Test
import uk.gov.onelogin.network.auth.TokenExchange.Companion.TokenExchangeCodeArgError
import uk.gov.onelogin.network.auth.TokenExchange.Companion.TokenExchangeOfflineError
import uk.gov.onelogin.network.utils.IOnlineChecker

class TokenExchangeTest {
    class OnlineCheckerStub: IOnlineChecker {
        var online: Boolean = true

        override fun isOnline(): Boolean = online
    }

    private val stubOnlineChecker = OnlineCheckerStub()

    @Before
    fun setup() {
        stubOnlineChecker.online = true
    }

    @Test(expected = TokenExchangeCodeArgError::class)
    fun `throws a TokenExchangeCodeArgError when an empty code is passed`() {
        TokenExchange(
            code = "",
            onlineChecker = stubOnlineChecker
        )
    }

    @Test(expected = TokenExchangeOfflineError::class)
    fun `throws a TokenExchangeOfflineError when the device is offline`() {
        stubOnlineChecker.online = false

        TokenExchange(
            code = "aCode",
            onlineChecker = stubOnlineChecker
        )
    }
}
