package uk.gov.onelogin.features.wallet.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.wallet.data.WalletRepository

@RunWith(AndroidJUnit4::class)
class WalletScreenTest : FragmentActivityTestCase() {
    private val walletSdk: WalletSdk = mock()
    private val walletRepository: WalletRepository = mock()

    private val viewModel = WalletScreenViewModel(
        walletSdk,
        walletRepository
    )

    @Test
    fun homeScreenDisplayed() {
        whenever(walletRepository.getWalletDeepLinkPathState()).thenReturn(false)
        whenever(walletSdk.displayAsFullScreen).thenReturn(MutableStateFlow(true))
        composeTestRule.setContent {
            WalletScreen(
                false,
                setDisplayContentAsFullScreen = { true },
                viewModel = viewModel
            )

            verify(walletSdk).WalletApp()
            verify(walletRepository, times(0)).toggleWallDeepLinkPathState()
        }
    }

    @Test
    fun walletSdkCalledWithDeeplink() {
        whenever(walletRepository.getWalletDeepLinkPathState()).thenReturn(true)
        whenever(walletSdk.displayAsFullScreen).thenReturn(MutableStateFlow(true))
        composeTestRule.setContent {
            WalletScreen(
                true,
                setDisplayContentAsFullScreen = { true },
                viewModel = viewModel
            )

            verify(walletSdk).WalletApp()
        }
    }
}
