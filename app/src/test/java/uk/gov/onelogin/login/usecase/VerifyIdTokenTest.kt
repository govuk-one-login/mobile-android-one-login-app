package uk.gov.onelogin.login.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.network.client.StubHttpClient
import uk.gov.onelogin.extensions.MainDispatcherExtension
import uk.gov.onelogin.tokens.verifier.JwtVerifier

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherExtension::class)
class VerifyIdTokenTest {
    private lateinit var stubHttpClient: GenericHttpClient
    private lateinit var stubVerifier: JwtVerifier
    private lateinit var verifyIdToken: VerifyIdToken

    // just the header of a web token which contains a 'kid' for one of the keys below
    private val idToken =
        "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCIsImtpZCI" +
            "6IjE2ZGI2NTg3LTU0NDUtNDVkNi1hN2Q5LTk4NzgxZWJkZjkzZCJ9.eyJhd"
    private val jwksResponse = "{\n" +
        "  \"keys\": [\n" +
        "    {\n" +
        "      \"kty\": \"EC\",\n" +
        "      \"x\": \"nfKPgSUMcrJ96ejGHr-tAvfzZOgLuFK-W_pz3Jjcs-Y\",\n" +
        "      \"y\": \"Z7xBQNM9ipvaDp1Lp3DNAn7RWQ_JaUBXstcXnefLR5k\",\n" +
        "      \"crv\": \"P-256\",\n" +
        "      \"use\": \"sig\",\n" +
        "      \"alg\": \"ES256\",\n" +
        "      \"kid\": \"16db6587-5445-45d6-a7d9-98781ebdf93d\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"kty\": \"RSA\",\n" +
        "      \"n\": \"DYuXw\",\n" +
        "      \"e\": \"AQAB\",\n" +
        "      \"use\": \"enc\",\n" +
        "      \"alg\": \"RSA-OAEP-256\",\n" +
        "      \"kid\": \"849bb6a3-eb58-471a-b279-75be3c60601b\"\n" +
        "    }\n" +
        "  ]\n" +
        "}"

    @Test
    fun `non 200 response from jwks endpoint`() = runTest {
        setupHttpStub(ApiResponse.Failure(400, Exception()))
        stubVerifier = JwtVerifier.stub(false)
        buildVerifyToken()

        verifyIdToken(idToken, "testUrl") {
            assertFalse(it)
        }
    }

    @Test
    fun `unable to verify token`() = runTest {
        setupHttpStub(ApiResponse.Success(jwksResponse))
        stubVerifier = JwtVerifier.stub(false)
        buildVerifyToken()

        verifyIdToken(idToken, "testUrl") {
            assertFalse(it)
        }
    }

    @Test
    fun `verifier throws exception`() = runTest {
        setupHttpStub(ApiResponse.Success(jwksResponse))
        stubVerifier = JwtVerifier.throwable(Exception("fail"))
        buildVerifyToken()

        verifyIdToken(idToken, "testUrl") {
            assertFalse(it)
        }
    }

    @Test
    fun `idToken json parse throws exception`() = runTest {
        setupHttpStub(ApiResponse.Success("not a json"))
        stubVerifier = JwtVerifier.stub(true)
        buildVerifyToken()

        verifyIdToken("not an id token", "testUrl") {
            assertFalse(it)
        }
    }

    @Test
    fun `jwks json parse throws exception`() = runTest {
        setupHttpStub(ApiResponse.Success("not a json"))
        stubVerifier = JwtVerifier.stub(true)
        buildVerifyToken()

        verifyIdToken(idToken, "testUrl") {
            assertFalse(it)
        }
    }

    @Test
    fun `id_token successfully verified`() = runTest {
        setupHttpStub(ApiResponse.Success(jwksResponse))
        stubVerifier = JwtVerifier.stub(true)
        buildVerifyToken()

        verifyIdToken(idToken, "testUrl") {
            assertTrue(it)
        }
    }

    private fun setupHttpStub(response: ApiResponse) {
        stubHttpClient = StubHttpClient(response)
    }

    private fun buildVerifyToken() {
        verifyIdToken = VerifyIdTokenImpl(
            stubHttpClient,
            stubVerifier
        )
    }
}
