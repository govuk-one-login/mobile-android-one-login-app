package uk.gov.onelogin.tokens.usecases

import android.util.Log
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.onelogin.tokens.Keys

fun interface RemoveAllSecureStoreData {
    /**
     * Use case for removing all data from the secure store instance
     *
     * @param context Must be a FragmentActivity context (due to authentication prompt)
     */
    operator fun invoke(
        context: FragmentActivity
    )
}

class RemoveAllSecureStoreDataImpl @Inject constructor(
    private val secureStore: SecureStore
) : RemoveAllSecureStoreData {
    override fun invoke(context: FragmentActivity) {
        try {
            secureStore.delete(
                context = context,
                key = Keys.ACCESS_TOKEN_KEY
            )
            secureStore.delete(
                context = context,
                key = Keys.ID_TOKEN_KEY
            )
        } catch (e: SecureStorageError) {
            Log.e(this::class.simpleName, e.message, e)
        }
    }
}
