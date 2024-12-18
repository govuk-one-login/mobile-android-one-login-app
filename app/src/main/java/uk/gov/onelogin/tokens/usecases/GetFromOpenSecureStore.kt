package uk.gov.onelogin.tokens.usecases

import android.util.Log
import javax.inject.Inject
import javax.inject.Named
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore

fun interface GetFromOpenSecureStore {
    /**
     * Use case for getting data from an open secure store instance.
     *
     * @param key [String] value of key value pair/ pairs to retrieve
     * @return The saved [String]s correlating to each key requested are returned unless there is
     * an issue or one of the keys requested does not exist, in which case, null is returned
     */
    suspend operator fun invoke(
        vararg key: String
    ): Map<String, String>?
}

class GetFromOpenSecureStoreImpl @Inject constructor(
    @Named("Open")
    private val secureStore: SecureStore
) : GetFromOpenSecureStore {
    override suspend fun invoke(
        vararg key: String
    ): Map<String, String>? {
        return when (val retrievalEvent = secureStore.retrieve(*key)) {
            is RetrievalEvent.Failed -> {
                Log.e(this::class.simpleName, "Reason: ${retrievalEvent.reason}")
                null
            }
            is RetrievalEvent.Success -> retrievalEvent.value
        }
    }
}
