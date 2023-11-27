package uk.gov.onelogin.login.nonce

interface INonceGenerator {
    fun generate(): String
}
