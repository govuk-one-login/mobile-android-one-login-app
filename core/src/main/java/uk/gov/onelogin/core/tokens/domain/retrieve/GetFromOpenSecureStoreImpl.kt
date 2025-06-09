package uk.gov.onelogin.core.tokens.domain.retrieve

import javax.inject.Inject
import javax.inject.Named
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.logging.api.Logger

class GetFromOpenSecureStoreImpl @Inject constructor(
    @Named("Open")
    private val secureStore: SecureStore,

    private val logger: Logger
) : GetFromOpenSecureStore {
    override suspend fun invoke(vararg key: String): Map<String, String>? {
        return when (val retrievalEvent = secureStore.retrieve(*key)) {
            is RetrievalEvent.Failed -> {
                logger.error(this::class.simpleName.toString(), "Reason: ${retrievalEvent.reason}")
                null
            }
            is RetrievalEvent.Success -> retrievalEvent.value
        }
    }
}
