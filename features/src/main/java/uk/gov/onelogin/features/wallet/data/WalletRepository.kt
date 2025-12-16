package uk.gov.onelogin.features.wallet.data

interface WalletRepository {
    fun setWalletDeepLinkPathState(deepLink: Boolean)
    fun isWalletDeepLinkPath(): Boolean
}

class WalletRepositoryImpl : WalletRepository {
    private var isDeepLinkInit: Boolean = false

    override fun setWalletDeepLinkPathState(deepLink: Boolean) {
        this.isDeepLinkInit = deepLink
    }

    override fun isWalletDeepLinkPath() = isDeepLinkInit
}
