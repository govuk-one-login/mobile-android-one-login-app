package uk.gov.onelogin.features.login.domain.appintegrity

import uk.gov.android.authentication.integrity.pop.SignedPoP

class TestAppIntegrity(
    var attestationResult: AttestationResult = AttestationResult.Success("attestation-jwt"),
    var popResult: SignedPoP = SignedPoP.Success("pop-jwt"),
) : AppIntegrity {
    override suspend fun getClientAttestation(): AttestationResult = attestationResult

    override fun getProofOfPossession(): SignedPoP = popResult

    override suspend fun retrieveSavedClientAttestation(): String? = null
}
