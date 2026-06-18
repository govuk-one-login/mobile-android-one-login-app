package uk.gov.onelogin.features.login.domain.validateWalletStoreId

import uk.gov.logging.api.LogTagProvider
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.core.logging.ErrorKeys.actionKey
import uk.gov.onelogin.core.logging.ErrorKeys.componentKey
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.core.tokens.domain.retrieve.GetWalletStoreId
import javax.inject.Inject

/** Returns `true` if a wallet store ID is present */
class ValidateWalletStoreId
    @Inject
    constructor(
        private val getWalletStoreId: GetWalletStoreId,
        private val getPersistentId: GetPersistentId,
        private val logger: Logger
    ) : LogTagProvider {
        @Suppress("ExpensiveAssertion")
        suspend operator fun invoke(): Boolean {
            assert(getPersistentId() != null) {
                "PersistentId must be present to validate WalletStoreId"
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
