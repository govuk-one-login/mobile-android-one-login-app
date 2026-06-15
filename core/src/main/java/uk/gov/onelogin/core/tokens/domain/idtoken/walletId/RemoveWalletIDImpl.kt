package uk.gov.onelogin.core.tokens.domain.idtoken.walletId

import uk.gov.logging.api.LogTagProvider
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.core.logging.ErrorKeys.actionKey
import uk.gov.onelogin.core.logging.ErrorKeys.componentKey
import uk.gov.onelogin.core.tokens.domain.remove.RemoveFromOpenSecureStore
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class RemoveWalletIDImpl
    @Inject
    constructor(
        private val removeFromOpenSecureStore: RemoveFromOpenSecureStore,
        private val logger: Logger,
    ) : RemoveWalletID,
        LogTagProvider {
        override suspend fun removeWalletId() =
            try {
                removeFromOpenSecureStore.remove(WALLET_ID_KEY)
            } catch (e: CancellationException) {
                throw e
            } catch (e: OutOfMemoryError) {
                logError(RemoveWalletStoreIdException.RemoveWalletStoreIDFailed(e))
            }

        private fun logError(throwable: Throwable) {
            throwable.message?.let {
                logger.error(
                    message = it,
                    throwable = throwable,
                    actionKey(ACTION),
                    componentKey(COMPONENT),
                )
            }
        }

        internal sealed class RemoveWalletStoreIdException(
            override val message: String,
            override val cause: Throwable? = null,
        ) : RuntimeException() {
            class RemoveWalletStoreIDFailed(
                cause: Throwable
            ) : RemoveWalletStoreIdException(
                    "Failed to remove wallet store ID",
                    cause = cause
                )
        }

        companion object {
            private const val COMPONENT = "remove.wallet.store_id"
            private const val ACTION = "remove wallet store ID"
        }
    }
