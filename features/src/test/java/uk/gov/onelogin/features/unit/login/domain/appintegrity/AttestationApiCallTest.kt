package uk.gov.onelogin.features.unit.login.domain.appintegrity

import android.content.Context
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
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
import uk.gov.android.network.client.v2.GenericHttpResponse
import uk.gov.android.network.client.v2.GenericResponseException
import uk.gov.android.network.client.v2.StubHttpClient
import uk.gov.android.network.service.DefaultNetworkService
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrityException
import uk.gov.onelogin.features.login.domain.appintegrity.AttestationApiCall
import kotlin.test.assertTrue

class AttestationApiCallTest {
    private lateinit var context: Context
    private val stubHttpClient = StubHttpClient()
    private val networkService = DefaultNetworkService(stubHttpClient)

    private lateinit var assertionApiCall: AttestationCaller

    @BeforeEach
    fun setUp() {
        context = mock()
        assertionApiCall = AttestationApiCall(context, networkService)

        whenever(context.getString(any())).thenReturn("/endpoint")
        whenever(context.getString(any(), eq("/endpoint"))).thenAnswer { "www.testUrl.com" }
    }

    @Test
    fun `call() - Success`() =
        runTest {
            val expectedResult = AttestationResponse.Success("Success", 0)
            stubHttpClient.response = GenericHttpResponse(200, VALID_CLIENT_ATTESTATION)

            val result =
                assertionApiCall.call(
                    "",
                    jwk,
                )

            assertEquals(expectedResult, result)
        }

    @Test
    fun `call() - Failure with error message and 500 status code - server error`() =
        runTest {
            stubHttpClient.exception = GenericResponseException(
                GenericHttpResponse(AttestationApiCall.SERVER_ERROR, "error"),
                IllegalStateException(),
            )

            val result =
                assertionApiCall.call(
                    "",
                    jwk,
                )

            assertTrue(result is AttestationResponse.Failure)
            assertTrue(result.error is AppIntegrityException.ClientAttestationException)
            assertEquals(
                AppIntegrityException.AppIntegrityErrorType.INTERMITTENT,
                (result.error as AppIntegrityException.ClientAttestationException).type,
            )
        }

    @Test
    fun `call() - Failure without error message and 500 status code - invalid app check token`() =
        runTest {
            stubHttpClient.exception = GenericResponseException(
                GenericHttpResponse(AttestationApiCall.INVALID_APP_CHECK_TOKEN, "error"),
                IllegalStateException(),
            )

            val result =
                assertionApiCall.call(
                    "",
                    jwk,
                )

            assertTrue(result is AttestationResponse.Failure)
            assertTrue(result.error is AppIntegrityException.ClientAttestationException)
            assertEquals(
                AppIntegrityException.AppIntegrityErrorType.INTERMITTENT,
                (result.error as AppIntegrityException.ClientAttestationException).type,
            )
        }

    @Test
    fun `call() - Failure without error message and 500 status code - intermittent app check token`() =
        runTest {
            stubHttpClient.exception = GenericResponseException(
                GenericHttpResponse(AttestationApiCall.INTERMITTENT_SERVER_ERROR, "error"),
                IllegalStateException(),
            )

            val result =
                assertionApiCall.call(
                    "",
                    jwk,
                )

            assertTrue(result is AttestationResponse.Failure)
            assertTrue(result.error is AppIntegrityException.ClientAttestationException)
            assertEquals(
                AppIntegrityException.AppIntegrityErrorType.INTERMITTENT,
                (result.error as AppIntegrityException.ClientAttestationException).type,
            )
        }

    @Test
    fun `call() - Failure without error message and 400 status code - invalid public key jwk`() =
        runTest {
            stubHttpClient.exception = GenericResponseException(
                GenericHttpResponse(AttestationApiCall.INVALID_PUBLIC_KEY_JWK, "error"),
                IllegalStateException(),
            )

            val result =
                assertionApiCall.call(
                    "",
                    jwk,
                )

            assertTrue(result is AttestationResponse.Failure)
            assertTrue(result.error is AppIntegrityException.ClientAttestationException)
            assertEquals(
                AppIntegrityException.AppIntegrityErrorType.APP_CHECK_FAILED,
                (result.error as AppIntegrityException.ClientAttestationException).type,
            )
        }

    @Test
    fun `call() - Failure without error message and random status code`() =
        runTest {
            stubHttpClient.exception = GenericResponseException(
                GenericHttpResponse(301, "error"),
                IllegalStateException(),
            )

            val result =
                assertionApiCall.call(
                    "",
                    jwk,
                )

            assertTrue(result is AttestationResponse.Failure)
            assertTrue(result.error is AppIntegrityException.ClientAttestationException)
            assertEquals(
                AppIntegrityException.AppIntegrityErrorType.GENERIC,
                (result.error as AppIntegrityException.ClientAttestationException).type,
            )
        }

    @Test
    fun `call() - Failure when transport error occurs`() =
        runTest {
            stubHttpClient.exception = IOException()

            val result =
                assertionApiCall.call(
                    "",
                    jwk,
                )

            assertTrue(result is AttestationResponse.Failure)
            assertTrue(result.error is AppIntegrityException.ClientAttestationException)
            assertEquals(
                AppIntegrityException.AppIntegrityErrorType.GENERIC,
                (result.error as AppIntegrityException.ClientAttestationException).type,
            )
            assertEquals(AttestationApiCall.NETWORK_ERROR, result.reason)
        }

    @Test
    fun `call() - Json failure`() =
        runTest {
            stubHttpClient.response = GenericHttpResponse(200, INVALID_CLIENT_ATTESTATION)

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
