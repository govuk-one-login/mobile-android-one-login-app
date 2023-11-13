package uk.gov.onelogin.network.auth

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import uk.gov.onelogin.network.auth.TokenExchange.Companion.TokenExchangeCodeArgError
import uk.gov.onelogin.network.auth.TokenExchange.Companion.TokenExchangeOfflineError
import uk.gov.onelogin.network.auth.TokenExchange.Companion.TokenExchangeServerError
import uk.gov.onelogin.network.utils.IOnlineChecker

class TokenExchangeTest {
    class OnlineCheckerStub: IOnlineChecker {
        var online: Boolean = true

        override fun isOnline(): Boolean = online
    }

    private val stubOnlineChecker = OnlineCheckerStub()

    private val redirectUrl = Url("https://example.com/redirect")
    private val url = Url("https://example.com/token")
    private val httpClient = HttpClient(MockEngine{
        respond(
            content = "",
            status = HttpStatusCode.InternalServerError,
            headers = headersOf(
                HttpHeaders.ContentType, ContentType.Application.Json.toString()
            )
        )
    })
    @Before
    fun setup() {
        stubOnlineChecker.online = true
    }

    @Test(expected = TokenExchangeCodeArgError::class)
    fun `throws a TokenExchangeCodeArgError when an empty code is passed`() {
        TokenExchange(
            code = "",
            onlineChecker = stubOnlineChecker,
            redirectUrl = redirectUrl,
            url = url,
            httpClient = httpClient
        )
    }

    @Test(expected = TokenExchangeOfflineError::class)
    fun `throws a TokenExchangeOfflineError when the device is offline`() {
        stubOnlineChecker.online = false

        TokenExchange(
            code = "aCode",
            onlineChecker = stubOnlineChecker,
            redirectUrl = redirectUrl,
            url = url,
            httpClient = httpClient
        )


    }
    @Test(expected = TokenExchangeServerError::class)
    fun `throws a TokenExchangeServerError when a 500 range error is received`() = runBlocking {
        TokenExchange(
            code = "aCode",
            onlineChecker = stubOnlineChecker,
            redirectUrl = redirectUrl,
            url = url,
            httpClient = httpClient
        ).send()


    }
}
