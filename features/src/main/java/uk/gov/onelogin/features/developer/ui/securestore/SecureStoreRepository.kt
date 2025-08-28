package uk.gov.onelogin.features.developer.ui.securestore

interface SecureStoreRepository {
    fun overrideWalletDelete(override: Boolean)
    fun isWalletDeleteOverride(): Boolean
    fun enableLocalDataDeleteFail(failEnabled: Boolean)
    fun isLocalDataDeleteFailEnabled(): Boolean
}

class SecureStoreRepositoryImpl : SecureStoreRepository {
    private var overrideWalletData = false
    private var enableLocalDataDeleteFail = false

    override fun overrideWalletDelete(override: Boolean) {
        overrideWalletData = override
    }

    override fun isWalletDeleteOverride(): Boolean {
        return overrideWalletData
    }

    override fun enableLocalDataDeleteFail(failEnabled: Boolean) {
        enableLocalDataDeleteFail = failEnabled
    }

    override fun isLocalDataDeleteFailEnabled(): Boolean {
        return enableLocalDataDeleteFail
    }
}
