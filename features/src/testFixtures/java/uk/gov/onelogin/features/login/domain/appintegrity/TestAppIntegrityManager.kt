package uk.gov.onelogin.features.login.domain.appintegrity

import uk.gov.android.authentication.integrity.AppIntegrityManager
import uk.gov.android.authentication.integrity.appcheck.model.AttestationResponse
import uk.gov.android.authentication.integrity.pop.SignedPoP

class TestAppIntegrityManager(
    var attestationResponse: AttestationResponse = AttestationResponse.Success(
        "attestation-jwt",
        0,
    ),
    var popResult: SignedPoP = SignedPoP.Success("pop-jwt"),
    var verifyResult: Boolean = true,
    var expiry: Long? = null,
) : AppIntegrityManager {
    override suspend fun getAttestation(): AttestationResponse = attestationResponse

    override fun generatePoP(iss: String, aud: String): SignedPoP = popResult

    override fun verifyAttestationJwk(attestation: String): Boolean = verifyResult

    override fun getExpiry(attestation: String): Long? = expiry
}
