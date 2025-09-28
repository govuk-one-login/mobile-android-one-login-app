package uk.gov.onelogin.features.wallet.ui

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.wallet.data.WalletRepository

class WalletScreenViewModelTest {
    private val walletSdk: WalletSdk = mock()
    private val walletRepository: WalletRepository = mock()
    private val sut = WalletScreenViewModel(walletSdk, walletRepository)

    @Test
    fun `no wallet deeplink`() {
        // WHEN
        whenever(walletRepository.getWalletDeepLinkPathState()).thenReturn(false)
        sut.checkWalletEnabled()

        // THEN
        // Times 2 because once when is initialised, and once when called specifically - this test
        // all possible use cases when coming in via deeplink and when returning back to the tab from any others
        verify(walletRepository, times(0)).toggleWallDeepLinkPathState()
    }

    @Test
    fun `received wallet deeplink`() {
        // WHEN
        whenever(walletRepository.getWalletDeepLinkPathState()).thenReturn(true)
        sut.checkWalletEnabled()

        // THEN
        // Times 2 because once when is initialised, and once when called specifically - this test
        // all possible use cases when coming in via deeplink and when returning back to the tab from any others
        verify(walletRepository, times(1)).toggleWallDeepLinkPathState()
    }
}
