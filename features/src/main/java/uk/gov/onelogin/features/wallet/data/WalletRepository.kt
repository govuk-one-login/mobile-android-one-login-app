package uk.gov.onelogin.features.wallet.data

interface WalletRepository {
    fun addCredential(credential: String)
    fun getCredential(): String
    fun addDeepLinkPath(path: String?)
    fun getDeepLinkPath(): String
    fun resetDeepLinkPath()
    fun resetCredential()
}

class WalletRepositoryImpl : WalletRepository {
    private var credential: String = ""
    private var path: String = ""

    override fun addCredential(credential: String) {
        this.credential = credential
    }

    override fun getCredential() = credential

    override fun addDeepLinkPath(path: String?) {
        path?.let {
            this.path = it
        }
    }

    override fun getDeepLinkPath(): String {
        return path
    }

    override fun resetDeepLinkPath() {
        path = ""
    }

    override fun resetCredential() {
        credential = ""
    }
}
