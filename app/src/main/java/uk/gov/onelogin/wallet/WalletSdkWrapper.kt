package uk.gov.onelogin.wallet

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.developer.ui.securestore.SecureStoreDevOptionsRepository
import uk.gov.onelogin.wallet.mock.WalletMock

/**
 * Wrapper implementation of [WalletSdk] that dynamically delegates to either the real SDK or a mock
 * based on the runtime override in [SecureStoreDevOptionsRepository].
 *
 * This allows testing with Maestro by switching to a mock wallet implementation at build time.
 */
class WalletSdkWrapper(
    private val realWalletSdk: WalletSdk,
    private val mockWalletSdk: WalletMock,
    private val devOptionsRepository: SecureStoreDevOptionsRepository,
) : WalletSdk {

    private val wrappedSdk: WalletSdk
        get() = if (devOptionsRepository.isWalletMockOverride()) mockWalletSdk else realWalletSdk

    override val config: WalletSdk.ConfigurationV2
        get() = wrappedSdk.config

    override val displayAsFullScreen: StateFlow<Boolean>
        get() = wrappedSdk.displayAsFullScreen

    override suspend fun setDeeplink(deeplink: String) {
        wrappedSdk.setDeeplink(deeplink)
    }

    override fun setWalletStoreId(walletStoreId: String) {
        wrappedSdk.setWalletStoreId(walletStoreId)
    }

    override fun isEmpty(): Boolean = wrappedSdk.isEmpty()

    override suspend fun deleteWalletData(): Boolean = wrappedSdk.deleteWalletData()

    @Composable
    override fun WalletApp() {
        wrappedSdk.WalletApp()
    }
}
