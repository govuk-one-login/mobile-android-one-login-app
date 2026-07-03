package uk.gov.onelogin.features.unit.network.provider

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import uk.gov.android.authentication.integrity.pop.SignedPoP
import uk.gov.android.network.attestation.ClientAttestationResponse
import uk.gov.onelogin.features.login.domain.appintegrity.AttestationResult
import uk.gov.onelogin.features.login.domain.appintegrity.TestAppIntegrity
import uk.gov.onelogin.features.network.provider.ClientAttestationProviderImpl

class ClientAttestationProviderImplTest {
    private val appIntegrity = TestAppIntegrity()
    private val provider = ClientAttestationProviderImpl(appIntegrity)

    companion object {
        private const val CLIENT_ATTESTATION = "attestation-jwt"
        private const val CLIENT_ATTESTATION_POP = "pop-jwt"
    }

    @Test
    fun `getClientAttestation returns Success when attestation and PoP succeed`() =
        runTest {
            val result = provider.getClientAttestation()

            assertEquals(
                ClientAttestationResponse.Success(CLIENT_ATTESTATION, CLIENT_ATTESTATION_POP),
                result,
            )
        }

    @Test
    fun `getClientAttestation returns Success when attestation not required with saved attestation`() =
        runTest {
            appIntegrity.attestationResult = AttestationResult.NotRequired(CLIENT_ATTESTATION)

            val result = provider.getClientAttestation()

            assertEquals(
                ClientAttestationResponse.Success(CLIENT_ATTESTATION, CLIENT_ATTESTATION_POP),
                result,
            )
        }

    @Test
    fun `getClientAttestation returns Success with empty attestation when not required and no saved attestation`() =
        runTest {
            appIntegrity.attestationResult = AttestationResult.NotRequired(null)

            val result = provider.getClientAttestation()

            assertEquals(
                ClientAttestationResponse.Success("", CLIENT_ATTESTATION_POP),
                result,
            )
        }

    @Test
    fun `getClientAttestation returns Failure when attestation fails`() =
        runTest {
            val error = Exception("attestation error")
            appIntegrity.attestationResult = AttestationResult.Failure(error)

            val result = provider.getClientAttestation()

            assertInstanceOf(ClientAttestationResponse.Failure::class.java, result)
            assertEquals(error, (result as ClientAttestationResponse.Failure).error.cause)
        }

    @Test
    fun `getClientAttestation returns Failure when PoP fails`() =
        runTest {
            val error = Exception("pop error")
            appIntegrity.popResult = SignedPoP.Failure("pop error", error)

            val result = provider.getClientAttestation()

            assertInstanceOf(ClientAttestationResponse.Failure::class.java, result)
            assertEquals(error, (result as ClientAttestationResponse.Failure).error.cause)
        }
}
