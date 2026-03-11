package uk.gov.onelogin.features.developer.ui.securestore

import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.error.SecureStorageErrorV2
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.domain.remove.RemoveAllSecureStoreData
import javax.inject.Inject
import javax.inject.Named

class RemoveAllSecureStoreDataDevOption
    @Inject
    constructor(
        @param:Named("Token")
        private val tokenSecureStore: SecureStoreAsyncV2,
        @param:Named("Open")
        private val openSecureStore: SecureStoreAsyncV2,
        private val secureStoreRepo: SecureStoreDevOptionsRepository,
        private val logger: Logger,
    ) : RemoveAllSecureStoreData {
        override suspend fun clean(): Result<Unit> =
            if (secureStoreRepo.isLocalDataDeleteFailEnabled()) {
                Result.failure(SecureStorageErrorV2(Exception("Simulate secure storage error")))
            } else {
                try {
                    tokenSecureStore.deleteAll()
                    openSecureStore.deleteAll()
                    Result.success(Unit)
                } catch (e: SecureStorageErrorV2) {
                    logger.error(this::class.simpleName.toString(), e.message.toString(), e)
                    Result.failure(e)
                }
            }
    }
