package uk.gov.onelogin.core.tokens.data.initialise

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.android.securestore.SecureStorageConfiguration
import uk.gov.android.securestore.SecureStore
import uk.gov.onelogin.core.tokens.domain.save.SaveTokens
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import uk.gov.onelogin.core.tokens.utils.DefaultDispatcher
import uk.gov.onelogin.core.tokens.utils.LocalAuthPrefsMapper
import javax.inject.Inject
import javax.inject.Named

class AutoInitialiseSecureStoreImpl
    @Inject
    constructor(
        private val localAuthManager: LocalAuthManager,
        private val saveTokens: SaveTokens,
        @Named("Token")
        private val secureStore: SecureStore,
        @ApplicationContext
        private val context: Context,
        @DefaultDispatcher
        private val coroutineContext: CoroutineDispatcher,
    ) : AutoInitialiseSecureStore {
        /**
         * Initializes the secure store and saves the tokens stored in the TokenRepository (in temporary memory).
         *
         * @param refreshToken - the refresh token cannot be stored in temporary memory, so it is required when processing ti if it's available to be passed in directly
         */
        override suspend fun initialise(refreshToken: String?) {
            val localAuthPref = localAuthManager.localAuthPreference
            if (localAuthPref == null || localAuthPref == LocalAuthPreference.Disabled) return
            val configuration =
                SecureStorageConfiguration(
                    AuthTokenStoreKeys.TOKEN_SECURE_STORE_ID,
                    LocalAuthPrefsMapper.mapAccessControlLevel(localAuthPref),
                )
            withContext(coroutineContext) {
                secureStore.init(context, configuration)
                saveTokens.save(refreshToken)
            }
        }
    }
