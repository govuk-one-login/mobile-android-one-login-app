package uk.gov.onelogin.login.usecase

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import uk.gov.android.network.api.ApiFailureReason
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.network.client.StubHttpClient
import uk.gov.onelogin.tokens.verifier.JwtVerifier

class VerifyIdTokenTest {
    private lateinit var stubHttpClient: GenericHttpClient
    private lateinit var stubVerifier: JwtVerifier
    private lateinit var verifyIdToken: VerifyIdToken

    // the header of the web token contains a 'kid' for one of the keys below
    private val idToken =
        "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjE2ZGI2NTg3LTU0NDUtNDVkNi1hN2Q5LTk4NzgxZ" +
            "WJkZjkzZCJ9.eyJhdWQiOiJHRVV6a0V6SVFVOXJmYmdBWmJzal9fMUVOUU0iLCJpc3MiOiJodHRwcz" +
            "ovL3Rva2VuLmJ1aWxkLmFjY291bnQuZ292LnVrIiwic3ViIjoiOWQwZjIxZGUtMmZkNy00MjdiLWE2" +
            "ZGYtMDdjZDBkOTVlM2I2IiwicGVyc2lzdGVudF9pZCI6ImNjODkzZWNlLWI2YmQtNDQ0ZC05YmI0LW" +
            "RlYzZmNTc3OGU1MCIsImlhdCI6MTcyMTk5ODE3OCwiZXhwIjoxNzIxOTk4MzU4LCJub25jZSI6InRl" +
            "c3Rfbm9uY2UiLCJlbWFpbCI6Im1vY2tAZW1haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWV9.G" +
            "1uQ9z2i-214kEmmtK7hEHRsgqJdk7AXjz_CaJDiuuqSyHZ4W48oE1karDBA-pKWpADdBpHeUC-eCj" +
            "jfBObjOg"
    private val idTokenMissingEmail =
        "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCIsImtpZCI" +
            "6IjE2ZGI2NTg3LTU0NDUtNDVkNi1hN2Q5LTk4NzgxZWJkZjkzZCJ9" +
            ".eyJhd"
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
        setupHttpStub(ApiResponse.Failure(ApiFailureReason.General,400, Exception()))
        stubVerifier = JwtVerifier.stub(false)
        buildVerifyToken()

        val result = verifyIdToken(idToken, "testUrl")
        assertFalse(result)
    }

    @Test
    fun `unable to verify token`() = runTest {
        setupHttpStub(ApiResponse.Success(jwksResponse))
        stubVerifier = JwtVerifier.stub(false)
        buildVerifyToken()

        val result = verifyIdToken(idToken, "testUrl")
        assertFalse(result)
    }

    @Test
    fun `verifier throws exception`() = runTest {
        setupHttpStub(ApiResponse.Success(jwksResponse))
        stubVerifier = JwtVerifier.throwable(Exception("fail"))
        buildVerifyToken()

        val result = verifyIdToken(idToken, "testUrl")
        assertFalse(result)
    }

    @Test
    fun `idToken json parse throws exception`() = runTest {
        setupHttpStub(ApiResponse.Success("not a json"))
        stubVerifier = JwtVerifier.stub(true)
        buildVerifyToken()

        val result = verifyIdToken("not an id token", "testUrl")
        assertFalse(result)
    }

    @Test
    fun `jwks json parse throws exception`() = runTest {
        setupHttpStub(ApiResponse.Success("not a json"))
        stubVerifier = JwtVerifier.stub(true)
        buildVerifyToken()

        val result = verifyIdToken(idToken, "testUrl")
        assertFalse(result)
    }

    @Test
    fun `id_token successfully verified`() = runTest {
        setupHttpStub(ApiResponse.Success(jwksResponse))
        stubVerifier = JwtVerifier.stub(true)
        buildVerifyToken()

        val result = verifyIdToken(idToken, "testUrl")
        assertTrue(result)
    }

    @Test
    fun `verify fails - email missing`() = runTest {
        setupHttpStub(ApiResponse.Success(idTokenMissingEmail))
        stubVerifier = JwtVerifier.stub(true)
        buildVerifyToken()

        val result = verifyIdToken(idToken, "testUrl")
        assertFalse(result)
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
