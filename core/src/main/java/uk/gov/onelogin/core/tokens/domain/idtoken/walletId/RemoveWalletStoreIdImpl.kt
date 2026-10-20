package uk.gov.onelogin.core.tokens.domain.idtoken.walletId

import javax.inject.Inject
import uk.gov.logging.api.LogTagProvider
import uk.gov.onelogin.core.tokens.domain.remove.RemoveFromOpenSecureStore

class RemoveWalletStoreIdImpl
@Inject
constructor(private val removeFromOpenSecureStore: RemoveFromOpenSecureStore,) :
    RemoveWalletStoreId,
    LogTagProvider {
    override suspend fun invoke() = removeFromOpenSecureStore.remove(WALLET_ID_KEY)
}
