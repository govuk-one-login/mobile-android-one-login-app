package uk.gov.onelogin.tokens.usecases

import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import javax.inject.Named
import uk.gov.android.onelogin.R
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.authentication.AuthenticatorPromptConfiguration
import uk.gov.android.securestore.error.SecureStoreErrorType
import uk.gov.onelogin.login.state.LocalAuthStatus

fun interface GetFromTokenSecureStore {
    /**
     * Use case for getting data from a secure store instance.
     *
     * @param context Must be a FragmentActivity context (due to authentication prompt)
     * @param key [String] value of key value pairs to retrieve
     * @param callback which is used for handling [LocalAuthStatus] result
     */
    suspend operator fun invoke(
        context: FragmentActivity,
        vararg key: String,
        callback: (LocalAuthStatus) -> Unit
    )
}

class GetFromTokenSecureStoreImpl @Inject constructor(
    @Named("Token")
    private val secureStore: SecureStore
) : GetFromTokenSecureStore {
    override suspend fun invoke(
        context: FragmentActivity,
        vararg key: String,
        callback: (LocalAuthStatus) -> Unit
    ) {
        val authPromptConfig = AuthenticatorPromptConfiguration(
            title = context.getString(R.string.app_authenticationDialogueTitle)
        )

        val result = secureStore.retrieveWithAuthentication(
            key = key,
            authPromptConfig = authPromptConfig,
            context = context
        )
        when (result) {
            is RetrievalEvent.Success -> callback(LocalAuthStatus.Success(result.value))

            is RetrievalEvent.Failed -> {
                val localAuthStatus = when (result.type) {
                    SecureStoreErrorType.GENERAL -> LocalAuthStatus.SecureStoreError

                    SecureStoreErrorType.USER_CANCELED_BIO_PROMPT ->
                        LocalAuthStatus.UserCancelled

                    SecureStoreErrorType.FAILED_BIO_PROMPT -> LocalAuthStatus.BioCheckFailed
                    SecureStoreErrorType.NOT_FOUND -> LocalAuthStatus.SecureStoreError
                }
                callback(localAuthStatus)
            }
        }
    }
}
