package uk.gov.onelogin.tokens.usecases

import android.util.Log
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import uk.gov.android.securestore.SecureStorageConfiguration
import uk.gov.android.securestore.SecureStorageError
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.SharedPrefsStore
import uk.gov.android.securestore.authentication.AuthenticatorPromptConfiguration
import uk.gov.onelogin.R
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.Mapper

fun interface GetFromSecureStore {
    /**
     * Use case for getting data from a secure store instance using id [Keys.SECURE_STORE_ID].
     * The creation of the secure store handled and only created if [BiometricPreference] set
     *
     * @param context Must be a FragmentActivity context (due to authentication prompt)
     * @param key [String] value of key value pair to save
     * @return [String] value saved in the store
     */
    suspend operator fun invoke(
        context: FragmentActivity,
        key: String
    ): String?
}

class GetFromSecureStoreImpl @Inject constructor(
    private val biometricPreferenceHandler: BiometricPreferenceHandler
) : GetFromSecureStore {
    override suspend fun invoke(context: FragmentActivity, key: String): String? {
        val bioPref = biometricPreferenceHandler.getBioPref()
        if (bioPref == null || bioPref == BiometricPreference.NONE) return null
        val configuration = SecureStorageConfiguration(
            Keys.SECURE_STORE_ID,
            Mapper.mapAccessControlLevel(bioPref)
        )
        val secureStore: SecureStore = SharedPrefsStore().apply {
            init(context, configuration)
        }
        val authPromptConfig = AuthenticatorPromptConfiguration(
            title = context.getString(R.string.app_authenticationDialogueTitle)
        )
        return try {
            secureStore.retrieveWithAuthentication(
                key = key,
                authPromptConfig = authPromptConfig,
                context = context
            )
        } catch (e: SecureStorageError) {
            Log.e(this::class.simpleName, e.message, e)
            null
        }
    }
}
