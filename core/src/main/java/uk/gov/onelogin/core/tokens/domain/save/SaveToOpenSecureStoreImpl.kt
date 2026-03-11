package uk.gov.onelogin.core.tokens.domain.save

import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.error.SecureStorageErrorV2
import uk.gov.logging.api.Logger
import javax.inject.Inject
import javax.inject.Named

class SaveToOpenSecureStoreImpl
    @Inject
    constructor(
        @param:Named("Open")
        private val secureStore: SecureStoreAsyncV2,
        private val logger: Logger,
    ) : SaveToOpenSecureStore {
        override suspend fun save(
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

        override suspend fun save(
            key: String,
            value: Number,
        ) {
            try {
                secureStore.upsert(
                    key = key,
                    value = value.toString(),
                )
            } catch (e: SecureStorageErrorV2) {
                logger.error(e::class.simpleName.toString(), e.message.toString(), e)
            }
        }
    }
