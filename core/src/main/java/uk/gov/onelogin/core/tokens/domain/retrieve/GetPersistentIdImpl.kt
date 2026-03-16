package uk.gov.onelogin.core.tokens.domain.retrieve

import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.logging.api.v2.Logger
import uk.gov.logging.api.v2.errorKeys.ErrorKeys
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import javax.inject.Inject

class GetPersistentIdImpl
    @Inject
    constructor(
        val getFromOpenSecureStore: GetFromOpenSecureStore,
        private val walletSdk: WalletSdk,
        private val logger: Logger,
    ) : GetPersistentId {
        override suspend fun invoke(): String?
            {
                val id = getFromOpenSecureStore(AuthTokenStoreKeys.PERSISTENT_ID_KEY)?.get(
                    AuthTokenStoreKeys.PERSISTENT_ID_KEY,
                )
                try {
                    if (id.isNullOrEmpty() && !walletSdk.isEmpty()) {
                        val exp = Exception("Wallet is not empty")
                        logger.error(
                            this.javaClass.simpleName,
                            exp.message ?: "error",
                            exp,
                            ErrorKeys.StringKey("reason", "secure wallet data deleted")
                        )
                    }
                } catch (e: Throwable) {
                    logger.error(
                        this.javaClass.simpleName,
                        e.message ?: "error",
                        e,
                        ErrorKeys.StringKey("reason", "secure wallet data deleted")
                    )
                }
                return id
            }
    }
