package uk.gov.onelogin.core.tokens.domain.remove

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.error.SecureStorageErrorV2
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.TOKEN_SHARED_PREFS
import javax.inject.Inject
import javax.inject.Named

class RemoveRefreshTokenAndExpiryImpl
    @Inject
    constructor(
        @ApplicationContext
        context: Context,
        @param:Named("Token")
        private val tokenSecureStore: SecureStoreAsyncV2,
        private val logger: Logger,
    ) : RemoveRefreshTokenAndExpiry {
        private val sharedPrefs =
            context.getSharedPreferences(TOKEN_SHARED_PREFS, Context.MODE_PRIVATE)

        override suspend fun remove() {
            sharedPrefs.edit {
                remove(REFRESH_TOKEN_EXPIRY_KEY)
                commit()
            }

            try {
                tokenSecureStore.delete(AuthTokenStoreKeys.REFRESH_TOKEN_KEY)
                Result.success(Unit)
            } catch (e: SecureStorageErrorV2) {
                logger.error(e::class.simpleName.toString(), e.message.toString(), e)
            }
        }
    }
