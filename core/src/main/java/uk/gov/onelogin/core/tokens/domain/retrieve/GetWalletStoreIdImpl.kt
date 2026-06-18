package uk.gov.onelogin.core.tokens.domain.retrieve

import uk.gov.logging.api.LogTagProvider
import uk.gov.onelogin.core.tokens.domain.idtoken.walletId.WALLET_ID_KEY
import javax.inject.Inject

/**
 *  The [GetWalletStoreIdImpl] retrieves the Wallet Store ID from the open secure store.
 */
class GetWalletStoreIdImpl
    @Inject
    constructor(
        private val getFromOpenSecureStore: GetFromOpenSecureStore,
    ) : GetWalletStoreId,
        LogTagProvider {
        override suspend fun invoke(): String? = getFromOpenSecureStore.invoke(WALLET_ID_KEY)?.get(WALLET_ID_KEY)
    }
