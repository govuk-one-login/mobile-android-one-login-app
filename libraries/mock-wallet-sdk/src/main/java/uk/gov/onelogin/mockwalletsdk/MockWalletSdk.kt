package uk.gov.onelogin.mockwalletsdk

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow
import uk.gov.android.wallet.sdk.WalletSdk

/**
 * Mock implementation of [WalletSdk] for testing.
 *
 * This mock implementation provides fake data and a local fake UI composable
 * for [WalletApp] to allow testing without requiring a real wallet SDK instantiation.
 */
class MockWalletSdk(
    private val configValue: WalletSdk.ConfigurationV2,
    private val displayAsFullScreenFlow: StateFlow<Boolean>,
) : WalletSdk {

    internal var walletStoreId: String? = null

    override val config: WalletSdk.ConfigurationV2
        get() = configValue

    override val displayAsFullScreen: StateFlow<Boolean>
        get() = displayAsFullScreenFlow

    override suspend fun setDeeplink(deeplink: String) {
        // No-op for mock - wallet mock doesn't handle deeplinks yet
    }

    override fun setWalletStoreId(walletStoreId: String) {
        this.walletStoreId = walletStoreId
    }

    override fun isEmpty(): Boolean = walletStoreId.isNullOrEmpty()

    override suspend fun deleteWalletData(): Boolean {
        this.walletStoreId = null
        return true
    }

    @Composable
    override fun WalletApp() {
        MockWalletContent()
    }
}
