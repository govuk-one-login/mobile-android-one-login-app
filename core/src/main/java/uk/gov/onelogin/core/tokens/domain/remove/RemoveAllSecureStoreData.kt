package uk.gov.onelogin.core.tokens.domain.remove

import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.error.SecureStorageErrorV2
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.cleaner.domain.Cleaner
import javax.inject.Inject
import javax.inject.Named

interface RemoveAllSecureStoreData : Cleaner

class RemoveAllSecureStoreDataImpl
    @Inject
    constructor(
        @param:Named("Token")
        private val tokenSecureStore: SecureStoreAsyncV2,
        @param:Named("Open")
        private val openSecureStore: SecureStoreAsyncV2,
        private val logger: Logger,
    ) : RemoveAllSecureStoreData {
        override suspend fun clean(): Result<Unit> =
            try {
                tokenSecureStore.deleteAll()
                openSecureStore.deleteAll()
                Result.success(Unit)
            } catch (e: SecureStorageErrorV2) {
                logger.error(e::class.simpleName.toString(), e.message.toString(), e)
                Result.failure(e)
            }
    }
