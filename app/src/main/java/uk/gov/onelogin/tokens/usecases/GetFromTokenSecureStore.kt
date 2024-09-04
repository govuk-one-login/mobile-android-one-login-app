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
     * @param key [String] value of key value pair to save
     * @return [Flow<RetrievalEvent>]
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

        when (
            val event = secureStore.retrieveWithAuthentication(
                key = key,
                authPromptConfig = authPromptConfig,
                context = context
            )
        ) {
            is RetrievalEvent.Success -> {
                println("success from secure store")
                callback(LocalAuthStatus.Success(event.values))
            }

            is RetrievalEvent.Failed -> {
                println("failure from secure store")
                val localAuthStatus = when (event.type) {
                    SecureStoreErrorType.GENERAL -> LocalAuthStatus.SecureStoreError

                    SecureStoreErrorType.USER_CANCELED_BIO_PROMPT ->
                        LocalAuthStatus.UserCancelled

                    SecureStoreErrorType.FAILED_BIO_PROMPT -> LocalAuthStatus.BioCheckFailed
                }
                callback(localAuthStatus)
            }
        }
    }
}
