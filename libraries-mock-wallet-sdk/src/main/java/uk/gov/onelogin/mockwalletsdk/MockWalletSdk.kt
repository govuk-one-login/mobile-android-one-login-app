package uk.gov.onelogin.mockwalletsdk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.gov.android.ui.componentsv2.warning.GdsWarningText
import uk.gov.android.ui.theme.m3.ExtraTypography
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.wallet.sdk.WalletSdk

/**
 * Mock implementation of [WalletSdk] for testing.
 *
 * This mock implementation provides fake data and a local fake UI composable
 * for [WalletApp] to allow testing without requiring a real wallet SDK instantiation.
 * It tracks deletion calls to enable UI assertions in automated tests.
 */
open class MockWalletSdk(
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
        MockWalletContent(deleteCount = count)
    }
}

@Composable
private fun MockWalletContent(deleteCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        GdsTheme {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Mock Wallet (Test Build)",
                    style = ExtraTypography.headlineLargeReg,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                if (deleteCount > 0) {
                    GdsWarningText(
                        text = "Wallet delete() method has been called $deleteCount times",
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MockWalletNoDeletePreview() {
    MockWalletContent(deleteCount = 0)
}

@Preview(showBackground = true)
@Composable
private fun MockWalletDeletePreview() {
    MockWalletContent(deleteCount = 1)
}
