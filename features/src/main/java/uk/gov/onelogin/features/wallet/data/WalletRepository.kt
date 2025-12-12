package uk.gov.onelogin.features.wallet.data

interface WalletRepository {
    fun setWalletDeepLinkPathState(deepLink: Boolean)
    fun isWalletDeepLinkPath(): Boolean
}

class WalletRepositoryImpl : WalletRepository {
    private var isDeepLinkInit: Boolean = false

    override fun setWalletDeepLinkPathState(deepLink: Boolean) {
        println(
            "WalletRepositoryImpl: setWalletDeepLinkPathState $deepLink before set " +
                "isDeepLinkInit: $isDeepLinkInit"
        )
        this.isDeepLinkInit = deepLink
        println(
            "WalletRepositoryImpl: setWalletDeepLinkPathState $deepLink after set " +
                "isDeepLinkInit: $isDeepLinkInit"
        )
    }

    override fun isWalletDeepLinkPath() = isDeepLinkInit
}
