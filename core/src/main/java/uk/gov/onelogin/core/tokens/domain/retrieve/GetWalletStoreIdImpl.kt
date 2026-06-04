package uk.gov.onelogin.core.tokens.domain.retrieve

import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.core.logging.ErrorKeys.actionKey
import uk.gov.onelogin.core.logging.ErrorKeys.componentKey
import uk.gov.onelogin.core.utils.WALLET_ID_KEY
import javax.inject.Inject

/**
 *  The [GetWalletStoreIdImpl] retrieves the Wallet Store ID from the open secure store.
 */
@Suppress("TooGenericExceptionCaught")
class GetWalletStoreIdImpl
    @Inject
    constructor(
        private val getFromOpenSecureStore: GetFromOpenSecureStore,
        private val logger: Logger,
    ) : GetWalletStoreId {
        override suspend fun invoke(): String? =
            try {
                val result = getFromOpenSecureStore.invoke(WALLET_ID_KEY)?.get(WALLET_ID_KEY)
                if (result.isNullOrEmpty()) {
                    logError(GetWalletStoreIdException.WalletStoreIdMissing())
                }
                result
            } catch (e: Throwable) {
                logError(GetWalletStoreIdException.RetrievalFailed(e))
                null
            }

        private fun logError(exception: GetWalletStoreIdException) =
            when (exception) {
                is GetWalletStoreIdException.WalletStoreIdMissing ->
                    logger.error(
                        this::class.java.simpleName,
                        exception.message,
                        exception,
                        componentKey("wallet.store_id"),
                        actionKey("Get wallet store ID"),
                    )
                is GetWalletStoreIdException.RetrievalFailed ->
                    logger.error(
                        this::class.java.simpleName,
                        exception.message,
                        exception.cause!!,
                        componentKey("wallet.store_id"),
                        actionKey("Get wallet store ID"),
                    )
            }
    }

internal sealed class GetWalletStoreIdException(
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException() {
    class WalletStoreIdMissing :
        GetWalletStoreIdException(
            "Wallet store ID is missing from device storage"
        )

    class RetrievalFailed(
        cause: Throwable
    ) : GetWalletStoreIdException(
            "Failed to retrieve wallet store ID",
            cause = cause
        )
}
