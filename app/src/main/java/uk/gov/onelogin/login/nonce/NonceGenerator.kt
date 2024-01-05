package uk.gov.onelogin.login.nonce

import java.util.UUID
import javax.inject.Inject

class NonceGenerator @Inject constructor() : INonceGenerator {
    override fun generate(): String = UUID.randomUUID().toString()
}
