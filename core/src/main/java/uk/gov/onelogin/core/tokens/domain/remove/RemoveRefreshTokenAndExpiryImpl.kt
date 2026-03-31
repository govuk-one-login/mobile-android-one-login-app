package uk.gov.onelogin.core.tokens.domain.remove

import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.error.SecureStorageErrorV2
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import javax.inject.Inject
import javax.inject.Named

class RemoveRefreshTokenAndExpiryImpl
    @Inject
    constructor(
        @param:Named("Token")
        private val tokenSecureStore: SecureStoreAsyncV2,
        @param:Named("Open")
        private val openSecureStore: SecureStoreAsyncV2,
        private val logger: Logger,
    ) : RemoveRefreshTokenAndExpiry {
        override suspend fun remove() {
            try {
                openSecureStore.delete(AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY)
                tokenSecureStore.delete(AuthTokenStoreKeys.REFRESH_TOKEN_KEY)
                Result.success(Unit)
            } catch (e: SecureStorageErrorV2) {
                logger.error(e::class.simpleName.toString(), e.message.toString(), e)
            }
        }
    }
