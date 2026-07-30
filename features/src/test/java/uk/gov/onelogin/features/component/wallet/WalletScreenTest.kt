package uk.gov.onelogin.features.component.wallet

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.core.tokens.domain.retrieve.GetWalletStoreId
import uk.gov.onelogin.core.ui.wallet.WalletAppDisplayer
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.wallet.ui.WalletScreen
import uk.gov.onelogin.features.wallet.ui.WalletScreenViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class WalletScreenTest : FragmentActivityTestCase() {
    private val walletSdk: WalletSdk = mock()
    private val getWalletStoreId: GetWalletStoreId = mock()
    private val walletAppDisplayer: WalletAppDisplayer = {
        Text("Stub Wallet SDK")
    }
    val viewModel = WalletScreenViewModel(
        walletSdk = walletSdk,
        getWalletStoreId = getWalletStoreId,
        walletAppDisplayer = walletAppDisplayer,
    )

    @Test
    fun homeScreenDisplayed() = runTest {
        whenever(getWalletStoreId.invoke()).thenReturn("test-store-id")
        whenever(walletSdk.displayAsFullScreen).thenReturn(MutableStateFlow(true))

        composeTestRule.setContent {
            WalletScreen(
                false,
                setDisplayContentAsFullScreen = { true },
                viewModel = viewModel
            )
        }

        composeTestRule
            .onNodeWithText("Stub Wallet SDK")
            .assertIsDisplayed()
    }
}
