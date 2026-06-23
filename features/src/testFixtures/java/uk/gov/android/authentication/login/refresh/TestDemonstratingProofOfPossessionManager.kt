package uk.gov.android.authentication.login.refresh

class TestDemonstratingProofOfPossessionManager(
    var response: SignedDPoP = SignedDPoP.Success("dpop-jwt"),
) : DemonstratingProofOfPossessionManager {
    var spyHtu: String? = null

    override fun generateDPoP(htu: String): SignedDPoP {
        spyHtu = htu
        return response
    }
}
