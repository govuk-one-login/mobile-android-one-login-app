package uk.gov.onelogin.login.nonce

class NonceGeneratorStub(
    private val nonce: String
): INonceGenerator {
    override fun generate(): String = nonce
}
