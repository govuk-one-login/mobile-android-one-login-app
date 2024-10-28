package uk.gov.onelogin.integrity

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.security.KeyPairGenerator
import java.security.KeyStore

internal class KeystoreManager {
    private val ks: KeyStore = KeyStore.getInstance(KEYSTORE).apply {
        load(null)
    }

    init {
        if (!ks.containsAlias(ALIAS)) {
            Log.d(this::class.simpleName, "Generating key pair")
            createNewKeys()
        }
    }

    private fun createNewKeys() {
        val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            "AndroidKeyStore"
        )
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            ALIAS,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        ).run {
            setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            build()
        }

        kpg.initialize(parameterSpec)
        kpg.genKeyPair()
    }

    companion object {
        private const val ALIAS = "app_check_keys"
        private const val KEYSTORE = "AndroidKeyStore"
    }
}
