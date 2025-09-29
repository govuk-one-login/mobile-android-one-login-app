package uk.gov.onelogin.features.wallet.data

interface WalletRepository {
    fun toggleWallDeepLinkPathState()
    fun isWalletDeepLinkPath(): Boolean
}

class WalletRepositoryImpl : WalletRepository {
    private var isDeepLinkInit: Boolean = false

    override fun toggleWallDeepLinkPathState() {
        this.isDeepLinkInit = !isDeepLinkInit
    }

    override fun isWalletDeepLinkPath() = isDeepLinkInit
}
