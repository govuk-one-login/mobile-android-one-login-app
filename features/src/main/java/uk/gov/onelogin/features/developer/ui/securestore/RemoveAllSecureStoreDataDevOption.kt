package uk.gov.onelogin.features.developer.ui.securestore

import javax.inject.Inject
import javax.inject.Named
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.domain.remove.RemoveAllSecureStoreData

class RemoveAllSecureStoreDataDevOption @Inject constructor(
    @Named("Token")
    private val tokenSecureStore: SecureStore,
    @Named("Open")
    private val openSecureStore: SecureStore,
    private val secureStoreRepo: SecureStoreRepository,
    private val logger: Logger
) : RemoveAllSecureStoreData {
    override suspend fun clean(): Result<Unit> {
        return if (secureStoreRepo.isLocalDataDeleteFailEnabled()) {
            Result.failure(SecureStorageError(Exception("Simulate secure storage error")))
        } else {
            try {
                tokenSecureStore.deleteAll()
                openSecureStore.deleteAll()
                Result.success(Unit)
            } catch (e: SecureStorageError) {
                logger.error(this::class.simpleName.toString(), e.message.toString(), e)
                Result.failure(e)
            }
        }
    }
}
