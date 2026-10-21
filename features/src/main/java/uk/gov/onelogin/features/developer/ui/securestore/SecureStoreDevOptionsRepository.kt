package uk.gov.onelogin.features.developer.ui.securestore

import uk.gov.android.onelogin.features.BuildConfig

interface SecureStoreDevOptionsRepository {
    fun overrideWalletDelete(override: Boolean)

    fun isWalletDeleteOverride(): Boolean

    fun enableLocalDataDeleteFail(failEnabled: Boolean)

    fun isLocalDataDeleteFailEnabled(): Boolean

    fun overrideWalletMock(override: Boolean)

    fun isWalletMockOverride(): Boolean
}

class SecureStoreDevOptionsRepositoryImpl : SecureStoreDevOptionsRepository {
    private var overrideWalletData = false
    private var enableLocalDataDeleteFail = false
    private var overrideWalletMock = BuildConfig.BUILD_WALLET_MOCK

    override fun overrideWalletDelete(override: Boolean) {
        overrideWalletData = override
    }

    override fun isWalletDeleteOverride(): Boolean = overrideWalletData

    override fun enableLocalDataDeleteFail(failEnabled: Boolean) {
        enableLocalDataDeleteFail = failEnabled
    }

    override fun isLocalDataDeleteFailEnabled(): Boolean = enableLocalDataDeleteFail

    override fun overrideWalletMock(override: Boolean) {
        overrideWalletMock = override
    }

    override fun isWalletMockOverride(): Boolean = overrideWalletMock
}
