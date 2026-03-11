package uk.gov.onelogin.core.tokens.domain.retrieve

import androidx.fragment.app.FragmentActivity
import uk.gov.android.onelogin.core.R
import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.authentication.AuthenticatorPromptConfiguration
import uk.gov.android.securestore.error.SecureStorageErrorV2
import uk.gov.android.securestore.error.SecureStoreErrorTypeV2
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.data.LocalAuthStatus
import javax.inject.Inject
import javax.inject.Named

class GetFromEncryptedSecureStoreImpl
    @Inject
    constructor(
        @param:Named("Token")
        private val secureStore: SecureStoreAsyncV2,
        private val logger: Logger,
    ) : GetFromEncryptedSecureStore {
        override suspend fun invoke(
            context: FragmentActivity,
            vararg key: String,
            callback: (LocalAuthStatus) -> Unit,
        ) {
            val authPromptConfig =
                AuthenticatorPromptConfiguration(
                    title = context.getString(R.string.app_authenticationDialogueTitle),
                )

            try {
                val result =
                    secureStore.retrieveWithAuthentication(
                        key = key,
                        authPromptConfig = authPromptConfig,
                        context = context,
                    )
                callback(LocalAuthStatus.Success(result))
            } catch (e: SecureStorageErrorV2) {
                logger.error(
                    this.javaClass.simpleName,
                    "${e.message} - type: ${e.type}",
                    e
                )
                val localAuthStatus =
                    when (e.type) {
                        SecureStoreErrorTypeV2.UNRECOVERABLE -> LocalAuthStatus.ReauthRequired

                        SecureStoreErrorTypeV2.NO_LOCAL_AUTH_ENABLED -> LocalAuthStatus.UnrecoverableError

                        SecureStoreErrorTypeV2.RECOVERABLE,
                        SecureStoreErrorTypeV2.USER_CANCELLED -> LocalAuthStatus.UserCancelledBioPrompt
                    }
                callback(localAuthStatus)
            }
        }
    }
