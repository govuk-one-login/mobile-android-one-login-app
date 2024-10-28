package uk.gov.onelogin.integrity

import uk.gov.onelogin.integrity.model.AttestationResponse
import uk.gov.onelogin.integrity.model.SignedResponse

interface ClientAttestationManager {
    suspend fun getAttestation(): AttestationResponse
    suspend fun signAttestation(attestation: String): SignedResponse
}
