package uk.gov.onelogin.features.login.domain.appintegrity

import android.content.Context
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.integrity.appcheck.model.AttestationResponse
import uk.gov.android.authentication.integrity.appcheck.usecase.AttestationCaller
import uk.gov.android.authentication.json.jwk.JWK
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import java.io.IOException
import kotlin.test.assertTrue

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
    fun `call() - Success`() =
        runBlocking {
            val expectedResult = AttestationResponse.Success("Success", 0)

            whenever(httpClient.makeRequest(any()))
                .thenReturn(ApiResponse.Success(VALID_CLIENT_ATTESTATION))

            val result =
                assertionApiCall.call(
                    "",
                    jwk
                )

            assertEquals(expectedResult, result)
        }

    @Test
    fun `call() - Failure with error message and 500 status code - server error`() =
        runBlocking {
            val error: AppIntegrity.AppIntegrityException =
                AppIntegrity.AppIntegrityException.ClientAttestationException(
                    IOException("Test error message"),
                    AppIntegrity.AppIntegrityException.AppIntegrityErrorType.INTERMITTENT
                )
            val expectedResult = AttestationResponse.Failure(error.e.message!!, error = error)
            whenever(httpClient.makeRequest(any()))
                .thenReturn(
                    ApiResponse.Failure(
                        AttestationApiCall.SERVER_ERROR,
                        error.e as Exception
                    )
                )

            val result =
                assertionApiCall.call(
                    "",
                    jwk
                )

            assertEquals(expectedResult, result)
        }

    @Test
    fun `call() - Failure without error message and 500 status code - invalid app check token`() =
        runBlocking {
            val error: AppIntegrity.AppIntegrityException =
                AppIntegrity.AppIntegrityException.ClientAttestationException(
                    IOException(),
                    AppIntegrity.AppIntegrityException.AppIntegrityErrorType.INTERMITTENT
                )
            val expectedResult =
                AttestationResponse
                    .Failure(AttestationApiCall.NETWORK_ERROR, error = error)
            whenever(httpClient.makeRequest(any()))
                .thenReturn(
                    ApiResponse.Failure(
                        AttestationApiCall.INVALID_APP_CHECK_TOKEN,
                        error.e as Exception
                    )
                )

            val result =
                assertionApiCall.call(
                    "",
                    jwk
                )

            assertEquals(expectedResult, result)
        }

    @Test
    fun `call() - Failure without error message and 500 status code - intermittent app check token`() =
        runBlocking {
            val error: AppIntegrity.AppIntegrityException =
                AppIntegrity.AppIntegrityException.ClientAttestationException(
                    IOException(),
                    AppIntegrity.AppIntegrityException.AppIntegrityErrorType.INTERMITTENT
                )
            val expectedResult =
                AttestationResponse
                    .Failure(AttestationApiCall.NETWORK_ERROR, error = error)
            whenever(httpClient.makeRequest(any()))
                .thenReturn(
                    ApiResponse.Failure(
                        AttestationApiCall.INTERMITTENT_SERVER_ERROR,
                        error.e as Exception
                    )
                )

            val result =
                assertionApiCall.call(
                    "",
                    jwk
                )

            assertEquals(expectedResult, result)
        }

    @Test
    fun `call() - Failure without error message and 400 status code - invalid public key jwk`() =
        runBlocking {
            val error: AppIntegrity.AppIntegrityException =
                AppIntegrity.AppIntegrityException.ClientAttestationException(
                    IOException(),
                    AppIntegrity.AppIntegrityException.AppIntegrityErrorType.APP_CHECK_FAILED
                )
            val expectedResult =
                AttestationResponse
                    .Failure(AttestationApiCall.NETWORK_ERROR, error = error)
            whenever(httpClient.makeRequest(any()))
                .thenReturn(
                    ApiResponse.Failure(
                        AttestationApiCall.INVALID_PUBLIC_KEY_JWK,
                        error.e as Exception
                    )
                )

            val result =
                assertionApiCall.call(
                    "",
                    jwk
                )

            assertEquals(expectedResult, result)
        }

    @Test
    fun `call() - Failure without error message and random status code`() =
        runBlocking {
            val error: AppIntegrity.AppIntegrityException =
                AppIntegrity.AppIntegrityException.ClientAttestationException(
                    IOException(),
                    AppIntegrity.AppIntegrityException.AppIntegrityErrorType.GENERIC
                )
            val expectedResult =
                AttestationResponse
                    .Failure(AttestationApiCall.NETWORK_ERROR, error = error)
            whenever(httpClient.makeRequest(any()))
                .thenReturn(
                    ApiResponse.Failure(
                        301,
                        error.e as Exception
                    )
                )

            val result =
                assertionApiCall.call(
                    "",
                    jwk
                )

            assertEquals(expectedResult, result)
        }

    @Test
    fun `call() - Failure when ApiResponse is Offline`() =
        runBlocking {
            val expectedResult =
                AttestationResponse
                    .Failure(
                        AttestationApiCall.NETWORK_ERROR,
                        AppIntegrity.AppIntegrityException.ClientAttestationException(
                            kotlin.Exception(AttestationApiCall.NETWORK_ERROR)
                        )
                    )
            whenever(httpClient.makeRequest(any()))
                .thenReturn(ApiResponse.Offline)

            val result =
                assertionApiCall.call(
                    "",
                    jwk
                )

            // Test all the values since the reason error has a hash wo it will never be able to test the error as a whole
            assertTrue(result is AttestationResponse.Failure)
            assertTrue(result.error is AppIntegrity.AppIntegrityException.ClientAttestationException)
            assertEquals(
                (expectedResult.error as AppIntegrity.AppIntegrityException.ClientAttestationException).type,
                (result.error as AppIntegrity.AppIntegrityException.ClientAttestationException).type
            )
            println(result.reason)
            println(
                (expectedResult.error as AppIntegrity.AppIntegrityException.ClientAttestationException)
                    .type.name
            )
            assertTrue(
                result.error
                    .toString()
                    .contains(
                        (expectedResult.error as AppIntegrity.AppIntegrityException.ClientAttestationException)
                            .type.name
                    )
            )
        }

    @Test
    fun `call() - Json failure`() =
        runBlocking {
            whenever(httpClient.makeRequest(any()))
                .thenReturn(ApiResponse.Success(INVALID_CLIENT_ATTESTATION))

            val result = assertionApiCall.call("", jwk)

            assert((result as AttestationResponse.Failure).error!! is IllegalArgumentException)
        }

    companion object {
        private val jwk = JWK.generateJwk("x", "y")
        private const val INVALID_CLIENT_ATTESTATION =
            "{\"client_attestation\": \"Success\", " +
                "\"expires_in\": \"a\"}"
        private const val VALID_CLIENT_ATTESTATION =
            "{\"client_attestation\": \"Success\", " +
                "\"expires_in\": \"0\"}"
    }
}
