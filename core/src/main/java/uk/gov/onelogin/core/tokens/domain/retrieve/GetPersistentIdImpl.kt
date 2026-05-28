package uk.gov.onelogin.core.tokens.domain.retrieve

import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.logging.api.LogTagProvider
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.core.logging.ErrorKeys.actionKey
import uk.gov.onelogin.core.logging.ErrorKeys.componentKey
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import javax.inject.Inject

@Suppress("TooGenericExceptionCaught")
class GetPersistentIdImpl
    @Inject
    constructor(
        val getFromOpenSecureStore: GetFromOpenSecureStore,
        private val walletSdk: WalletSdk,
        private val logger: Logger,
    ) : GetPersistentId,
        LogTagProvider {
        override suspend fun invoke(): String? {
            val id =
                getFromOpenSecureStore(AuthTokenStoreKeys.PERSISTENT_ID_KEY)?.get(
                    AuthTokenStoreKeys.PERSISTENT_ID_KEY,
                )

            // Log an error if the Wallet state is inconsistent with the persistent ID
            try {
                if (id.isNullOrEmpty() && !walletSdk.isEmpty()) {
                    logError(GetPersistentIdException.WalletNotEmpty())
                }
            } catch (walletError: WalletSdk.WalletSdkError.WalletEmptyCheckFailed) {
                logError(GetPersistentIdException.WalletEmptyCheckFailed(walletError))
            } catch (e: Throwable) {
                // We don't expect this path to be reachable
                logError(GetPersistentIdException.WalletEmptyCheckFailed(e))
            }

            return id
        }

        private fun logError(exception: GetPersistentIdException) =
            logger.error(
                exception.message,
                exception,
                componentKey("tokens.persistent_id"),
                actionKey("Get persistent ID"),
            )
    }

internal sealed class GetPersistentIdException(
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException() {
    class WalletEmptyCheckFailed(
        cause: Throwable
    ) : GetPersistentIdException(
            "Wallet SDK failed to check if wallet is empty",
            cause = cause
        )

    class WalletNotEmpty :
        GetPersistentIdException(
            "No persistent ID but wallet isn't empty"
        )
}
