package uk.gov.onelogin.wallet.mock

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow
import uk.gov.android.wallet.sdk.WalletSdk

/**
 * Mock implementation of [WalletSdk] for testing with Maestro.
 * 
 * This mock implementation provides no-op implementations for all methods
 * to allow testing without requiring a real wallet SDK connection.
 */
open class WalletMock(
    private val configValue: WalletSdk.ConfigurationV2,
    private val displayAsFullScreenFlow: StateFlow<Boolean>
) : WalletSdk {

    private var walletStoreId: String? = null

    override val config: WalletSdk.ConfigurationV2
        get() = configValue

    override val displayAsFullScreen: StateFlow<Boolean>
        get() = displayAsFullScreenFlow

    override suspend fun setDeeplink(deeplink: String) {
        // No-op for mock - wallet mock doesn't handle deeplinks
    }

    override fun setWalletStoreId(walletStoreId: String) {
        this.walletStoreId = walletStoreId
    }

    override fun isEmpty(): Boolean {
        return walletStoreId.isNullOrEmpty()

    }

    override suspend fun deleteWalletData(): Boolean {
        this.walletStoreId = null
        return true
    }

    @Composable
    override fun WalletApp() {
        // No-op / mock UI for test build
    }
}
