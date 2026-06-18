package uk.gov.onelogin.core.tokens.domain.idtoken.walletId

import uk.gov.logging.api.LogTagProvider
import uk.gov.onelogin.core.tokens.domain.remove.RemoveFromOpenSecureStore
import javax.inject.Inject

class RemoveWalletStoreIdImpl
    @Inject
    constructor(
        private val removeFromOpenSecureStore: RemoveFromOpenSecureStore,
    ) : RemoveWalletStoreId,
        LogTagProvider {
        override suspend fun invoke() = removeFromOpenSecureStore.remove(WALLET_ID_KEY)
    }
