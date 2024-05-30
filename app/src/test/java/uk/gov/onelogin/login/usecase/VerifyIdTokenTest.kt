package uk.gov.onelogin.login.usecase

import java.lang.Exception
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.network.client.StubHttpClient
import uk.gov.onelogin.tokens.verifier.JwtVerifier

class VerifyIdTokenTest {
    private lateinit var stubHttpClient: GenericHttpClient
    private lateinit var stubVerifier: JwtVerifier

    private lateinit var verifyIdToken: VerifyIdToken

    @Test
    fun `non 200 response from jwks endpoint`() = runTest {
        setupHttpStub(ApiResponse.Failure(400, Exception()))
        stubVerifier = JwtVerifier.stub(false)
        buildVerifyToken()

        val result = verifyIdToken("testToken")

        assertFalse(result)
    }

    @Test
    fun `unable to verify token`() = runTest {
        setupHttpStub(ApiResponse.Success("test"))
        stubVerifier = JwtVerifier.stub(false)
        buildVerifyToken()

        val result = verifyIdToken("testToken")

        assertFalse(result)
    }

    @Test
    fun `id_token successfully verified`() = runTest {
        setupHttpStub(ApiResponse.Success("test"))
        stubVerifier = JwtVerifier.stub(true)
        buildVerifyToken()

        val result = verifyIdToken("testToken")

        assertTrue(result)
    }

    private fun setupHttpStub(response: ApiResponse) {
        stubHttpClient = StubHttpClient(response)
    }

    private fun buildVerifyToken() {
        verifyIdToken = VerifyIdTokenImpl(
            "testUrl",
            stubHttpClient,
            stubVerifier
        )
    }
}
