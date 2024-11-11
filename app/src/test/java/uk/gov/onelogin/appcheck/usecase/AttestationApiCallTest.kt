package uk.gov.onelogin.appcheck.usecase

import android.content.Context
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.integrity.model.AttestationResponse
import uk.gov.android.authentication.integrity.usecase.AttestationCaller
import uk.gov.android.authentication.integrity.usecase.JWK
import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient

class AttestationApiCallTest {

    private lateinit var context: Context
    private lateinit var httpClient: GenericHttpClient

    private lateinit var assertionApiCall: AttestationCaller

    @BeforeEach
    fun setUp() {
        context = mock()
        httpClient = mock()
        assertionApiCall = AttestationApiCall(context, httpClient)

        whenever(context.getString(any())).thenReturn("/endpoint")
        whenever(context.getString(any(), eq("/endpoint"))).thenAnswer { "www.testUrl.com" }
    }

    @Test
    fun `call() - Success`() = runBlocking {
        val expectedResult = AttestationResponse.Success("Success", 0)
        val expectedRequest = ApiRequest.Get(
            url = "www.testUrl.com?device=android",
            headers = listOf(
                "X-Firebase-Token" to AttestationCaller.FIREBASE_HEADER
            )
        )

        whenever(httpClient.makeRequest(expectedRequest))
            .thenReturn(ApiResponse.Success("Success"))

        val result = assertionApiCall.call(
            "",
            jwk
        )

        assertEquals(expectedResult, result)
    }

    @Test
    fun `call() - Failure with error message`() = runBlocking {
        val error = Result.failure<Exception>(IOException("Test error message"))
        whenever(httpClient.makeRequest(any()))
            .thenReturn(ApiResponse.Failure(500, error.exceptionOrNull() as Exception))

        val result = assertionApiCall.call(
            "",
            jwk
        )

        assertEquals(error, result)
    }

    @Test
    fun `call() - Failure without error message`() = runBlocking {
        val error = Result.failure<Exception>(IOException())
        whenever(httpClient.makeRequest(any()))
            .thenReturn(ApiResponse.Failure(500, error.exceptionOrNull() as Exception))

        val result = assertionApiCall.call(
            "",
            jwk
        )

        assertEquals(error, result)
    }

    @Test
    fun `call() - Json failure`() = runBlocking {
        whenever(httpClient.makeRequest(any()))
            .thenReturn(ApiResponse.Success(INVALID_CLIENT_ATTESTATION))

        val result = assertionApiCall.call("", jwk)

        assert((result as AttestationResponse.Failure).error!! is IllegalArgumentException)
    }

    companion object {
        private val jwk = JWK.makeJWK("x", "y")
        private const val INVALID_CLIENT_ATTESTATION = "{\"client_attestation\": \"Success\", \"expires_in\": \"a\"}"
    }
}
