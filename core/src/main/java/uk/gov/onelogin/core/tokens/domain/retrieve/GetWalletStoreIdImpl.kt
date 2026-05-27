package uk.gov.onelogin.core.tokens.domain.retrieve

import javax.inject.Inject

class GetWalletStoreIdImpl
    @Inject
    constructor(
        private val getFromOpenSecureStore: GetFromOpenSecureStore
    ) : GetWalletStoreId {
        override suspend fun invoke(): String? = getFromOpenSecureStore.invoke(WALLET_ID_KEY)?.get(WALLET_ID_KEY)

        companion object {
            private const val WALLET_ID_KEY = "wallet_id"
        }
    }
