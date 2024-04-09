package uk.gov.onelogin.tokens.usecases

import android.util.Log
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import uk.gov.android.securestore.SecureStorageError
import uk.gov.android.securestore.SecureStore

fun interface SaveToSecureStore {
    /**
     * Use case for saving data into a secure store instance
     *
     * @param context Must be a FragmentActivity context (due to authentication prompt)
     * @param key [String] value of key value pair to save
     * @param value [String] value of value in key value pair to save
     */
    suspend operator fun invoke(
        context: FragmentActivity,
        key: String,
        value: String
    )
}

class SaveToSecureStoreImpl @Inject constructor(
    private val secureStore: SecureStore
) : SaveToSecureStore {
    override suspend fun invoke(context: FragmentActivity, key: String, value: String) {
        try {
            secureStore.upsert(
                context = context,
                key = key,
                value = value
            )
        } catch (e: SecureStorageError) {
            Log.e(this::class.simpleName, e.message, e)
        }
    }
}
