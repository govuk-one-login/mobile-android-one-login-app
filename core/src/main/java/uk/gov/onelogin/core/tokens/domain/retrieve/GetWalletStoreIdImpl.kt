package uk.gov.onelogin.core.tokens.domain.retrieve

import uk.gov.logging.api.LogTagProvider
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.core.logging.ErrorKeys.actionKey
import uk.gov.onelogin.core.logging.ErrorKeys.componentKey
import uk.gov.onelogin.core.tokens.domain.idtoken.walletId.WALLET_ID_KEY
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 *  The [GetWalletStoreIdImpl] retrieves the Wallet Store ID from the open secure store.
 */
class GetWalletStoreIdImpl
    @Inject
    constructor(
        private val getFromOpenSecureStore: GetFromOpenSecureStore,
        private val logger: Logger,
    ) : GetWalletStoreId,
        LogTagProvider {
        override suspend fun invoke(): String? =
            try {
                getFromOpenSecureStore.invoke(WALLET_ID_KEY)?.get(WALLET_ID_KEY)
            } catch (e: CancellationException) {
                throw e
            } catch (e: OutOfMemoryError) {
                logError(GetWalletStoreIdException.RetrievalFailed(e))
                null
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

        internal sealed class GetWalletStoreIdException(
            override val message: String,
            override val cause: Throwable? = null,
        ) : RuntimeException() {
            class RetrievalFailed(
                cause: Throwable
            ) : GetWalletStoreIdException(
                    "Failed to retrieve wallet store ID",
                    cause = cause
                )
        }

        companion object {
            private const val COMPONENT = "wallet.store_id"
            private const val ACTION = "Get wallet store ID"
        }
    }
