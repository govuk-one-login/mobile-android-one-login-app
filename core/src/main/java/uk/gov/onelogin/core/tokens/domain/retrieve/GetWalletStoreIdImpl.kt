package uk.gov.onelogin.core.tokens.domain.retrieve

import javax.inject.Inject
import uk.gov.onelogin.core.tokens.domain.idtoken.walletId.WALLET_ID_KEY

/**
 *  The [GetWalletStoreIdImpl]Retrieves the Wallet Store ID from the open secure store.
 */
class GetWalletStoreIdImpl
@Inject
constructor(
    private val getFromOpenSecureStore: GetFromOpenSecureStore,
) : GetWalletStoreId {
    override suspend fun invoke(): String? =
        getFromOpenSecureStore.invoke(WALLET_ID_KEY)?.get(WALLET_ID_KEY)
}
