package uk.gov.onelogin.features.login.domain.validateWalletStoreId

import uk.gov.logging.api.LogTagProvider
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.core.logging.ErrorKeys.actionKey
import uk.gov.onelogin.core.logging.ErrorKeys.componentKey
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.core.tokens.domain.retrieve.GetWalletStoreId
import javax.inject.Inject

/**
 * Checks that the wallet store ID is present, given that the user is already signed in.
 *
 * The wallet store ID must be saved before using the wallet.
 *
 * Since One Login app v1.17.0, the user's wallet store ID is saved at the point when a
 * user is signed in. However, users who signed in using a previous app version will not
 * have a wallet store ID.
 * [ValidateWalletStoreId] is just for detecting those users. We can then retrieve a
 * wallet store ID for them, migrating their session to the proper state.
 *
 * @property getWalletStoreId retrieves the wallet store ID from secure storage
 * @property getPersistentId retrieves the persistent ID
 * @property logger logs errors when wallet store ID is missing
 */
class ValidateWalletStoreId
    @Inject
    constructor(
        private val getWalletStoreId: GetWalletStoreId,
        private val getPersistentId: GetPersistentId,
        private val logger: Logger
    ) : LogTagProvider {
        /**
         * @return true if the wallet store ID is present, and false if not
         * @throws AssertionError if this use case is invoked when the user is not signed (only in debug builds)
         */
        @Suppress("ExpensiveAssertion")
        suspend operator fun invoke(): Boolean {
            assert(getPersistentId() != null) {
                "User must be signed-in before validating wallet store ID"
            }

            val walletStoreId = getWalletStoreId()

            if (walletStoreId.isNullOrEmpty()) {
                logError()
                return false
            }

            return true
        }

        private fun logError() {
            val throwable = WalletStoreIdMissingException()
            logger.error(
                WALLET_STORE_ID_MISSING,
                throwable = throwable,
                componentKey(COMPONENT),
                actionKey(ACTION)
            )
        }

        internal class WalletStoreIdMissingException : RuntimeException(WALLET_STORE_ID_MISSING)

        companion object {
            private const val COMPONENT = "wallet.store_id"
            private const val ACTION = "Get wallet store ID"
            private const val WALLET_STORE_ID_MISSING = "Wallet store ID is missing from device storage"
        }
    }
