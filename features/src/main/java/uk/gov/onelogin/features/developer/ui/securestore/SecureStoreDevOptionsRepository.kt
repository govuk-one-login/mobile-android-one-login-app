package uk.gov.onelogin.features.developer.ui.securestore

interface SecureStoreDevOptionsRepository {
    fun overrideWalletDelete(override: Boolean)

    fun isWalletDeleteOverride(): Boolean

    fun enableLocalDataDeleteFail(failEnabled: Boolean)

    fun isLocalDataDeleteFailEnabled(): Boolean
}

class SecureStoreDevOptionsRepositoryImpl : SecureStoreDevOptionsRepository {
    private var overrideWalletData = false
    private var enableLocalDataDeleteFail = false

    override fun overrideWalletDelete(override: Boolean) {
        overrideWalletData = override
    }

    override fun isWalletDeleteOverride(): Boolean = overrideWalletData

    override fun enableLocalDataDeleteFail(failEnabled: Boolean) {
        enableLocalDataDeleteFail = failEnabled
    }

    override fun isLocalDataDeleteFailEnabled(): Boolean = enableLocalDataDeleteFail
}
