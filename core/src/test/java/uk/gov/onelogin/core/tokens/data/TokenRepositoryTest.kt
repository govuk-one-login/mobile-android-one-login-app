package uk.gov.onelogin.core.tokens.data

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import uk.gov.onelogin.core.tokens.data.tokendata.LoginTokens
import kotlin.test.assertEquals

class TokenRepositoryTest {
    private lateinit var repo: TokenRepository

    @BeforeEach
    fun setup() {
        repo = TokenRepositoryImpl()
    }

    @Test
    fun `check set and retrieve`() {
        val testResponse =
            LoginTokens(
                tokenType = "test",
                accessToken = "test",
                accessTokenExpirationTime = 1L,
                idToken = "test",
            )
        repo.setTokenResponse(testResponse)

        assertEquals(testResponse, repo.getTokenResponse())
    }

    @Test
    fun `check null on retrieve`() {
        assertNull(repo.getTokenResponse())
    }

    @Test
    fun `test clearTokenResponse`() {
        // given a token is saved in the repository
        val testResponse =
            LoginTokens(
                tokenType = "test",
                accessToken = "test",
                accessTokenExpirationTime = 1L,
                idToken = "test",
            )
        repo.setTokenResponse(testResponse)

        // when clearTokenResponse called
        repo.clearTokenResponse()

        // repository is cleared
        assertTrue(repo.isTokenResponseClear())
    }
}
