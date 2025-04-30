package uk.gov.onelogin.features.wallet.data

interface WalletRepository {
    fun addCredential(credential: String)
    fun getCredential(): String
}

class WalletRepositoryImpl : WalletRepository {
    private var credential: String = ""

    override fun addCredential(credential: String) {
        this.credential = credential
    }

    override fun getCredential() = credential
}
