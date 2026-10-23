package uk.gov.onelogin.wallet.mock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.gov.android.wallet.sdk.WalletSdk

/**
 * Mock implementation of [WalletSdk] for testing with Maestro.
 *
 * The Mock implementation is designed in line with [Mobile Testing Mocks - Mobile SDK] recommendations [https://govukverify.atlassian.net/wiki/spaces/DCMAW/pages/7051450372/TD+DCMAW-20333+Mobile+Testing+Mocks+-+Mobile+SDK]
 *
 * This mock implementation provides fake data and a local fake UI composable
 * for [WalletApp] to allow testing without requiring a real wallet SDK connection.
 * It tracks deletion calls to enable UI assertions in automated tests.
 */
open class WalletMock(
    private val configValue: WalletSdk.ConfigurationV2,
    private val displayAsFullScreenFlow: StateFlow<Boolean>,
) : WalletSdk {

    private var walletStoreId: String? = null
    private val _deleteCallCount = MutableStateFlow(0)
    val deleteCallCount: StateFlow<Int> = _deleteCallCount.asStateFlow()

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
        _deleteCallCount.value += 1
        return true
    }

    @Composable
    override fun WalletApp() {
        val count by deleteCallCount.collectAsStateWithLifecycle()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Mock Wallet (Test Build)",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Black,
                )
                if (count > 0) {
                    Text(
                        text = "Wallet delete() method has been called $count times",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            }
        }
    }
}
