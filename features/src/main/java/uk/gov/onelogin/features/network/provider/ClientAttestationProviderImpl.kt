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
        override suspend fun getClientAttestation(): ClientAttestationResponse {
            val clientAttestation =
                when (val attestationResult = appIntegrity.getClientAttestation()) {
                    is AttestationResult.Success -> attestationResult.clientAttestation
                    // If client attestation isn't required and not cached then we may safely provide an empty string
                    is AttestationResult.NotRequired -> attestationResult.savedAttestation ?: ""
                    is AttestationResult.Failure -> return attestationResult.toFailure()
                }

            val attestationPop =
                when (val popResult = appIntegrity.getProofOfPossession()) {
                    is SignedPoP.Success -> popResult.popJwt
                    is SignedPoP.Failure -> return popResult.toFailure()
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
