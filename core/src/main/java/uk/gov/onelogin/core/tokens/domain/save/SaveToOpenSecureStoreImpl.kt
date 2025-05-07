package uk.gov.onelogin.core.tokens.domain.save

import javax.inject.Inject
import javax.inject.Named
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.utils.OneLoginInjectionAnnotation

class SaveToOpenSecureStoreImpl @Inject constructor(
    @Named("Open")
    private val secureStore: SecureStore,
    @OneLoginInjectionAnnotation
    private val logger: Logger
) : SaveToOpenSecureStore {
    override suspend fun save(
        key: String,
        value: String
    ) {
        try {
            secureStore.upsert(
                key = key,
                value = value
            )
        } catch (e: SecureStorageError) {
            logger.error(this::class.simpleName.toString(), e.message.toString(), e)
        }
    }

    override suspend fun save(
        key: String,
        value: Number
    ) {
        try {
            secureStore.upsert(
                key = key,
                value = value.toString()
            )
        } catch (e: SecureStorageError) {
            logger.error(this::class.simpleName.toString(), e.message.toString(), e)
        }
    }
}
