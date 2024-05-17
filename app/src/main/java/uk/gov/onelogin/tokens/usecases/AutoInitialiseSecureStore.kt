package uk.gov.onelogin.tokens.usecases

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import uk.gov.android.securestore.SecureStorageConfiguration
import uk.gov.android.securestore.SecureStore
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.Mapper

fun interface AutoInitialiseSecureStore {
    /**
     * Initialise the secure store using id [Keys.SECURE_STORE_ID].
     * The initialisation of the secure store only happens if [BiometricPreference] set
     */
    operator fun invoke()
}

class AutoInitialiseSecureStoreImpl @Inject constructor(
    private val biometricPreferenceHandler: BiometricPreferenceHandler,
    private val secureStore: SecureStore,
    @ApplicationContext
    private val context: Context
) : AutoInitialiseSecureStore {
    override fun invoke() {
        val bioPref = biometricPreferenceHandler.getBioPref()
        if (bioPref == null || bioPref == BiometricPreference.NONE) return
        val configuration = SecureStorageConfiguration(
            Keys.SECURE_STORE_ID,
            Mapper.mapAccessControlLevel(bioPref)
        )
        secureStore.init(context, configuration)
    }
}
