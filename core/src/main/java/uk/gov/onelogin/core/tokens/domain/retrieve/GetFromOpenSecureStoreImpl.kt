package uk.gov.onelogin.core.tokens.domain.retrieve

import javax.inject.Inject
import javax.inject.Named
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.data.SecureStoreException

class GetFromOpenSecureStoreImpl @Inject constructor(
    @Named("Open")
    private val secureStore: SecureStore,
    private val logger: Logger
) : GetFromOpenSecureStore {
    override suspend fun invoke(vararg key: String): Map<String, String>? {
        return when (val retrievalEvent = secureStore.retrieve(*key)) {
            is RetrievalEvent.Failed -> {
                val secureStoreException = SecureStoreException(
                    Exception(retrievalEvent.toString())
                )
                logger.error(
                    secureStoreException::class.simpleName.toString(),
                    retrievalEvent.toString(),
                    secureStoreException
                )
                null
            }
            is RetrievalEvent.Success -> retrievalEvent.value
        }
    }
}
