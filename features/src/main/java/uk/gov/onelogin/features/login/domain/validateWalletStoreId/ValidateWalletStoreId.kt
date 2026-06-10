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
        @Suppress("ExpensiveAssertion", "TooGenericExceptionCaught")
        suspend operator fun invoke(): Boolean =
            try {
                assert(getPersistentId() != null) {
                    "PersistentId must be present to validate WalletStoreId"
                }

                val walletStoreIdValid = getWalletStoreId()

                if (walletStoreIdValid.isNullOrEmpty()) {
                    logError(ValidateWalletStoreIdException.WalletStoreIdMissing())
                    false
                } else {
                    true
                }
            } catch (e: Throwable) {
                logError(ValidateWalletStoreIdException.RetrievalFailed(e))
                false
            }

        private fun logError(throwable: Throwable) {
            throwable.message?.let {
                logger.error(
                    it,
                    throwable = throwable,
                    componentKey(COMPONENT),
                    actionKey(ACTION)
                )
            }
        }

        internal sealed class ValidateWalletStoreIdException(
            override val message: String,
            override val cause: Throwable? = null,
        ) : RuntimeException() {
            class WalletStoreIdMissing :
                ValidateWalletStoreIdException(
                    "Wallet store ID is missing from device storage"
                )

            class RetrievalFailed(
                cause: Throwable
            ) : ValidateWalletStoreIdException(
                    "Failed to retrieve wallet store ID",
                    cause = cause
                )
        }

        companion object {
            private const val COMPONENT = "wallet.store_id"
            private const val ACTION = "Get wallet store ID"
        }
    }
