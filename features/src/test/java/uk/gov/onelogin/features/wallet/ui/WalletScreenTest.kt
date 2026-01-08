package uk.gov.onelogin.features.wallet.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class WalletScreenTest : FragmentActivityTestCase() {
    private val walletSdk: WalletSdk = mock()

    private val viewModel = WalletScreenViewModel(
        walletSdk
    )

    @Test
    fun homeScreenDisplayed() {
        whenever(walletSdk.displayAsFullScreen).thenReturn(MutableStateFlow(true))
        composeTestRule.setContent {
            WalletScreen(
                false,
                setDisplayContentAsFullScreen = { true },
                viewModel = viewModel
            )

            verify(walletSdk).WalletApp()
        }
    }
}
