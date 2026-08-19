package uk.gov.onelogin.features.component.wallet

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.core.tokens.domain.retrieve.GetWalletStoreId
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreenAnalyticsViewModel
import uk.gov.onelogin.core.ui.wallet.WalletDisplayer
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.wallet.ui.WalletScreen
import uk.gov.onelogin.features.wallet.ui.WalletScreenViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class WalletScreenTest : FragmentActivityTestCase() {
    private val walletSdk: WalletSdk = mock()
    private val getWalletStoreId: GetWalletStoreId = mock()
    private val loadingAnalyticsViewModel: LoadingScreenAnalyticsViewModel = mock()
    private val walletDisplayer: WalletDisplayer = {
        Text("Stub Wallet SDK")
    }

    private val viewModel by lazy {
        WalletScreenViewModel(
            walletSdk = walletSdk,
            getWalletStoreId = getWalletStoreId,
            walletDisplayer = walletDisplayer,
        )
    }

    @Before
    fun setUp() = runTest {
        whenever(getWalletStoreId.invoke()).thenReturn("test-store-id")
        whenever(walletSdk.displayAsFullScreen).thenReturn(MutableStateFlow(true))
    }

    @Test
    fun homeScreenDisplayed() = runTest {
        composeTestRule.setContent {
            WalletScreen(
                false,
                setDisplayContentAsFullScreen = { true },
                viewModel = viewModel,
                loadingAnalyticsViewModel = loadingAnalyticsViewModel,
            )
        }

        composeTestRule
            .onNodeWithText("Stub Wallet SDK")
            .assertIsDisplayed()
    }
}
