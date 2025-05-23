package uk.gov.onelogin.core.tokens.domain.remove

import javax.inject.Inject
import javax.inject.Named
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.cleaner.domain.Cleaner
import uk.gov.onelogin.core.utils.OneLoginInjectionAnnotation

interface RemoveAllSecureStoreData : Cleaner

class RemoveAllSecureStoreDataImpl @Inject constructor(
    @Named("Token")
    private val tokenSecureStore: SecureStore,
    @Named("Open")
    private val openSecureStore: SecureStore,
    @OneLoginInjectionAnnotation
    private val logger: Logger
) : RemoveAllSecureStoreData {
    override suspend fun clean(): Result<Unit> {
        return try {
            tokenSecureStore.deleteAll()
            openSecureStore.deleteAll()
            Result.success(Unit)
        } catch (e: SecureStorageError) {
            logger.error(this::class.simpleName.toString(), e.message.toString(), e)
            Result.failure(e)
        }
    }
}
