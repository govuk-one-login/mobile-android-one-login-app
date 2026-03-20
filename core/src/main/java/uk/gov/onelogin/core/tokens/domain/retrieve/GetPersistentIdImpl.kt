package uk.gov.onelogin.core.tokens.domain.retrieve

import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.logging.api.v2.Logger
import uk.gov.logging.api.v2.errorKeys.ErrorKeys
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import javax.inject.Inject

@Suppress("TooGenericExceptionCaught")
class GetPersistentIdImpl
    @Inject
    constructor(
        val getFromOpenSecureStore: GetFromOpenSecureStore,
        private val walletSdk: WalletSdk,
        private val logger: Logger,
    ) : GetPersistentId {
        override suspend fun invoke(): String? {
            val id =
                getFromOpenSecureStore(AuthTokenStoreKeys.PERSISTENT_ID_KEY)?.get(
                    AuthTokenStoreKeys.PERSISTENT_ID_KEY,
                )
            try {
                if (id.isNullOrEmpty() && !walletSdk.isEmpty()) {
                    val exp = Exception("Wallet is not empty")
                    val reason = "secure wallet data deleted"
                    logErrors(exp, reason)
                }
            } catch (walletError: WalletSdk.WalletSdkError.WalletEmptyCheckFailed) {
                val reason = walletError.message ?: "could not determine if wallet is empty"
                logErrors(walletError, reason)
            } catch (e: Throwable) {
                val reason = "secure wallet data deleted"
                logErrors(e, reason)
            }
            return id
        }

        private fun logErrors(
            errorThrown: Throwable,
            reason: String
        ) {
            logger.error(
                this.javaClass.simpleName,
                errorThrown.message ?: "error",
                errorThrown,
                ErrorKeys.StringKey("reason", reason)
            )
        }
    }
