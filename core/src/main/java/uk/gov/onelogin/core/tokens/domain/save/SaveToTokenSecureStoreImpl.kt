package uk.gov.onelogin.core.tokens.domain.save

import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.error.SecureStorageErrorV2
import uk.gov.logging.api.Logger
import javax.inject.Inject
import javax.inject.Named

class SaveToTokenSecureStoreImpl
    @Inject
    constructor(
        @param:Named("Token")
        private val secureStore: SecureStoreAsyncV2,
        private val logger: Logger,
    ) : SaveToTokenSecureStore {
        override suspend fun invoke(
            key: String,
            value: String,
        ) {
            try {
                secureStore.upsert(
                    key = key,
                    value = value,
                )
            } catch (e: SecureStorageErrorV2) {
                logger.error(e::class.simpleName.toString(), e.message.toString(), e)
            }
        }
    }
