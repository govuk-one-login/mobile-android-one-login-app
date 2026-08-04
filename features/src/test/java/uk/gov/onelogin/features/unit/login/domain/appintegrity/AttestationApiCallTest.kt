package uk.gov.onelogin.features.unit.login.domain.appintegrity

import android.content.Context
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.integrity.appcheck.model.AttestationResponse
import uk.gov.android.authentication.integrity.appcheck.usecase.AttestationCaller
import uk.gov.android.authentication.json.jwk.JWK
import uk.gov.android.network.client.v2.GenericHttpResponse
import uk.gov.android.network.service.v2.StubNetworkService
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrityException
import uk.gov.onelogin.features.login.domain.appintegrity.AttestationApiCall

class AttestationApiCallTest {
    private val context: Context = mock()
    private val networkService = StubNetworkService()

    private lateinit var assertionApiCall: AttestationCaller

    @BeforeEach
    fun setUp() {
        assertionApiCall = AttestationApiCall(context, networkService)

        whenever(context.getString(any())).thenReturn("/endpoint")
        whenever(context.getString(any(), eq("/endpoint"))).thenAnswer { "www.testUrl.com" }
    }

    @Test
    fun `call() - Success`() =
        runTest {
            givenApiSuccess()

            val result = assertionApiCall.call("", jwk)

            assertEquals(
                AttestationResponse.Success("Success", 0),
                result,
            )
        }

    @Test
    fun `call() - Failure with error message and 500 status code - server error`() =
        runTest {
            givenApiFailure(AttestationApiCall.SERVER_ERROR)

            val result = assertionApiCall.call("", jwk)

            result.assertFailure(AppIntegrityException.AppIntegrityErrorType.INTERMITTENT)
        }

    @Test
    fun `call() - Failure without error message and 500 status code - invalid app check token`() =
        runTest {
            givenApiFailure(AttestationApiCall.INVALID_APP_CHECK_TOKEN)

            val result = assertionApiCall.call("", jwk)

            result.assertFailure(AppIntegrityException.AppIntegrityErrorType.INTERMITTENT)
        }

    @Test
    fun `call() - Failure without error message and 500 status code - intermittent app check token`() =
        runTest {
            givenApiFailure(AttestationApiCall.INTERMITTENT_SERVER_ERROR)

            val result = assertionApiCall.call("", jwk)

            result.assertFailure(AppIntegrityException.AppIntegrityErrorType.INTERMITTENT)
        }

    @Test
    fun `call() - Failure without error message and 400 status code - invalid public key jwk`() =
        runTest {
            givenApiFailure(AttestationApiCall.INVALID_PUBLIC_KEY_JWK)

            val result = assertionApiCall.call("", jwk)

            result.assertFailure(AppIntegrityException.AppIntegrityErrorType.APP_CHECK_FAILED)
        }

    @Test
    fun `call() - Failure without error message and unexpected status code`() =
        runTest {
            givenApiFailure(301)

            val result = assertionApiCall.call("", jwk)

            result.assertFailure(AppIntegrityException.AppIntegrityErrorType.GENERIC)
        }

    @Test
    fun `call() - Json failure`() =
        runTest {
            networkService.setSuccessResponse(200, INVALID_CLIENT_ATTESTATION)

            val result = assertionApiCall.call("", jwk)

            result.assertFailure(AppIntegrityException.AppIntegrityErrorType.GENERIC)
        }
    private fun givenApiSuccess(
        body: String = VALID_CLIENT_ATTESTATION,
    ) {
        networkService.setSuccessResponse(200, body)
    }

    private fun givenApiFailure(
        status: Int
    ) {
        networkService.setFailureResponse(GenericHttpResponse(status, "error"))
    }
    private fun AttestationResponse.assertFailure(
        expectedType: AppIntegrityException.AppIntegrityErrorType,
    ) {
        assertInstanceOf<AttestationResponse.Failure>(this)
        val error = this.error
        assertInstanceOf<AppIntegrityException.ClientAttestationException>(error)
        assertEquals(expectedType, error.type)
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
