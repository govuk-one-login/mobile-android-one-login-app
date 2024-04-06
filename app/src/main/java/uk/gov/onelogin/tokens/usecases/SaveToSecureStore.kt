package uk.gov.onelogin.tokens.usecases

import android.util.Log
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import uk.gov.android.securestore.SecureStorageConfiguration
import uk.gov.android.securestore.SecureStorageError
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.SharedPrefsStore
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.Mapper

fun interface SaveToSecureStore {
    /**
     * Use case for saving data into a secure store instance using id [Keys.SECURE_STORE_ID].
     * The creation of the secure store handled and only created if [BiometricPreference] set
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
    private val biometricPreferenceHandler: BiometricPreferenceHandler
) : SaveToSecureStore {
    override suspend fun invoke(context: FragmentActivity, key: String, value: String) {
        biometricPreferenceHandler.getBioPref()?.let { bioPref ->
            if (bioPref == BiometricPreference.NONE) return
            val configuration = SecureStorageConfiguration(
                Keys.SECURE_STORE_ID,
                Mapper.mapAccessControlLevel(bioPref)
            )
            val secureStore: SecureStore = SharedPrefsStore()
            secureStore.init(context, configuration)
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
}
