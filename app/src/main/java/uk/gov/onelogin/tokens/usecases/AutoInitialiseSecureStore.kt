package uk.gov.onelogin.tokens.usecases

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import uk.gov.android.securestore.SecureStorageConfiguration
import uk.gov.android.securestore.SecureStore
import uk.gov.onelogin.core.DefaultDispatcher
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.login.usecase.SaveTokens
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.Mapper

fun interface AutoInitialiseSecureStore {
    /**
     * Initialise the token secure store using id [Keys.TOKEN_SECURE_STORE_ID].
     * The initialisation of the secure store only happens if BiometricPreference is set
     * to either [BiometricPreference.PASSCODE] or [BiometricPreference.BIOMETRICS].
     *
     * Also attempts to save any tokens in the [uk.gov.onelogin.repositiories.TokenRepository]
     */
    suspend fun initialise()
}

class AutoInitialiseSecureStoreImpl @Inject constructor(
    private val biometricPreferenceHandler: BiometricPreferenceHandler,
    private val saveTokens: SaveTokens,
    @Named("Token")
    private val secureStore: SecureStore,
    @ApplicationContext
    private val context: Context,
    @DefaultDispatcher
    private val coroutineContext: CoroutineDispatcher
) : AutoInitialiseSecureStore {
    override suspend fun initialise() {
        val bioPref = biometricPreferenceHandler.getBioPref()
        if (bioPref == null || bioPref == BiometricPreference.NONE) return
        val configuration = SecureStorageConfiguration(
            Keys.TOKEN_SECURE_STORE_ID,
            Mapper.mapAccessControlLevel(bioPref)
        )
        withContext(coroutineContext) {
            secureStore.init(context, configuration)
            saveTokens()
        }
    }
}
