package uk.gov.onelogin.core.tokens.domain.retrieve

import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.error.SecureStorageErrorV2
import uk.gov.logging.api.Logger
import javax.inject.Inject
import javax.inject.Named

class GetFromOpenSecureStoreImpl
    @Inject
    constructor(
        @param:Named("Open")
        private val secureStore: SecureStoreAsyncV2,
        private val logger: Logger,
    ) : GetFromOpenSecureStore {
        override suspend fun invoke(vararg key: String): Map<String, String?>? =
            try {
                secureStore.retrieve(*key)
            } catch (e: SecureStorageErrorV2) {
                logger.error(
                    e::class.simpleName.toString(),
                    "Attempting to retrieve $key \n Error: ${e.message} - type: ${e.type}",
                    e
                )
                null
            }
    }
