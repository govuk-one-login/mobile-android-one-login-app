package uk.gov.onelogin.core.tokens.domain.save

import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.logging.api.Logger
import javax.inject.Inject
import javax.inject.Named

class SaveToTokenSecureStoreImpl
    @Inject
    constructor(
        @Named("Token")
        private val secureStore: SecureStore,
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
            } catch (e: SecureStorageError) {
                logger.error(e::class.simpleName.toString(), e.message.toString(), e)
            }
        }
    }
