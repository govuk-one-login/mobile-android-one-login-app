package uk.gov.onelogin.tokens.usecases

import android.util.Log
import javax.inject.Inject
import javax.inject.Named
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.onelogin.tokens.Keys

fun interface RemoveAllSecureStoreData {
    /**
     * Use case for removing all data from the secure store instance
     */
    operator fun invoke()
}

class RemoveAllSecureStoreDataImpl @Inject constructor(
    @Named("Token")
    private val tokenSecureStore: SecureStore
) : RemoveAllSecureStoreData {
    override fun invoke() {
        try {
            tokenSecureStore.delete(
                key = Keys.ACCESS_TOKEN_KEY
            )
            tokenSecureStore.delete(
                key = Keys.ID_TOKEN_KEY
            )
        } catch (e: SecureStorageError) {
            Log.e(this::class.simpleName, e.message, e)
            throw e
        }
    }
}
