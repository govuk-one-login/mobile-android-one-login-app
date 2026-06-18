package uk.gov.onelogin.features.network.provider

import uk.gov.android.authentication.integrity.pop.SignedPoP
import uk.gov.android.network.attestation.ClientAttestationProvider
import uk.gov.android.network.attestation.ClientAttestationResponse
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity
import uk.gov.onelogin.features.login.domain.appintegrity.AttestationResult
import javax.inject.Inject

/**
 * [ClientAttestationProvider] implementation for the networking service.
 */
class ClientAttestationProviderImpl
    @Inject
    constructor(
        private val appIntegrity: AppIntegrity,
    ) : ClientAttestationProvider {
        @Suppress("ReturnCount")
        override suspend fun getClientAttestation(): ClientAttestationResponse {
            val clientAttestation =
                when (val result = appIntegrity.getClientAttestation()) {
                    is AttestationResult.Success -> result.clientAttestation
                    // If client attestation isn't required and not cached then we may safely provide an empty string
                    is AttestationResult.NotRequired -> result.savedAttestation ?: ""
                    is AttestationResult.Failure -> return result.toFailure()
                }

            val attestationPop =
                when (val result = appIntegrity.getProofOfPossession()) {
                    is SignedPoP.Success -> result.popJwt
                    is SignedPoP.Failure -> return result.toFailure()
                }

            return ClientAttestationResponse.Success(
                clientAttestation = clientAttestation,
                attestationPop = attestationPop,
            )
        }

        private fun AttestationResult.Failure.toFailure(): ClientAttestationResponse.Failure =
            ClientAttestationResponse.Failure(
                IllegalStateException("Failed to get client attestation", error),
            )

        private fun SignedPoP.Failure.toFailure(): ClientAttestationResponse.Failure =
            ClientAttestationResponse.Failure(
                IllegalStateException("Failed to get proof of possession because: $reason", error),
            )
    }
