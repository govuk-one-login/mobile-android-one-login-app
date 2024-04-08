package uk.gov.onelogin.tokens.usecases

import android.util.Log
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import uk.gov.android.securestore.SecureStorageError
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.authentication.AuthenticatorPromptConfiguration
import uk.gov.onelogin.R

fun interface GetFromSecureStore {
    /**
     * Use case for getting data from a secure store instance.
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
    private val secureStore: SecureStore
) : GetFromSecureStore {
    override suspend fun invoke(context: FragmentActivity, key: String): String? {
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
