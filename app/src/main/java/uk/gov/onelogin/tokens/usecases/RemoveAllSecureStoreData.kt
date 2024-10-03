package uk.gov.onelogin.tokens.usecases

import android.util.Log
import javax.inject.Inject
import javax.inject.Named
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.onelogin.core.delete.domain.Cleaner
import uk.gov.onelogin.tokens.Keys

interface RemoveAllSecureStoreData : Cleaner

class RemoveAllSecureStoreDataImpl @Inject constructor(
    @Named("Token")
    private val tokenSecureStore: SecureStore
) : RemoveAllSecureStoreData {
    override suspend fun clean(): Result<Unit> {
        return try {
            tokenSecureStore.delete(
                key = Keys.ACCESS_TOKEN_KEY
            )
            tokenSecureStore.delete(
                key = Keys.ID_TOKEN_KEY
            )
            Result.success(Unit)
        } catch (e: SecureStorageError) {
            Log.e(this::class.simpleName, e.message, e)
            Result.failure(e)
        }
    }
}
