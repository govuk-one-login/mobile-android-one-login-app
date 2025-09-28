package uk.gov.onelogin.features.wallet.ui

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag
import uk.gov.onelogin.features.wallet.data.WalletRepository

class WalletScreenViewModelTest {
    private val walletSdk: WalletSdk = mock()
    private val featureFlags: FeatureFlags = mock()
    private val walletRepository: WalletRepository = mock()
    private val sut = WalletScreenViewModel(walletSdk, featureFlags, walletRepository)

    @Test
    fun `wallet visible, no wallet deeplink`() {
        // Test initial state
        assertFalse(sut.walletEnabled.value)
        assertFalse(sut.isDeeplinkRoute.value)

        // WHEN
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(true)
        whenever(walletRepository.getWalletDeepLinkPathState()).thenReturn(false)
        sut.checkWalletEnabled()

        // THEN
        // Times 2 because once when is initialised, and once when called specifically - this test
        // all possible use cases when coming in via deeplink and when returning back to the tab from any others
        verify(walletRepository, times(0)).toggleWallDeepLinkPathState()
        assertTrue(sut.walletEnabled.value)
        assertFalse(sut.isDeeplinkRoute.value)
    }

    @Test
    fun `wallet visible, received wallet deeplink`() {
        // Test initial state
        assertFalse(sut.walletEnabled.value)
        assertFalse(sut.isDeeplinkRoute.value)

        // WHEN
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(true)
        whenever(walletRepository.getWalletDeepLinkPathState()).thenReturn(true)
        sut.checkWalletEnabled()

        // THEN
        // Times 2 because once when is initialised, and once when called specifically - this test
        // all possible use cases when coming in via deeplink and when returning back to the tab from any others
        verify(walletRepository, times(1)).toggleWallDeepLinkPathState()
        assertTrue(sut.walletEnabled.value)
        assertTrue(sut.isDeeplinkRoute.value)
    }

    @Test
    fun `wallet not visible, no wallet deeplink`() {
        // Test initial state
        assertFalse(sut.walletEnabled.value)

        // WHEN
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(false)
        whenever(walletRepository.getWalletDeepLinkPathState()).thenReturn(false)
        sut.checkWalletEnabled()

        // THEN
        verify(walletRepository, times(0)).toggleWallDeepLinkPathState()
        assertFalse(sut.walletEnabled.value)
        assertFalse(sut.isDeeplinkRoute.value)
    }

    @Test
    fun `wallet not visible, received wallet deeplink`() {
        // Test initial state
        assertFalse(sut.walletEnabled.value)

        // WHEN
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(false)
        whenever(walletRepository.getWalletDeepLinkPathState()).thenReturn(true)
        sut.checkWalletEnabled()

        // THEN
        verify(walletRepository, times(1)).toggleWallDeepLinkPathState()
        assertFalse(sut.walletEnabled.value)
        assertFalse(sut.isDeeplinkRoute.value)
    }

    @Test
    fun `wallet deeplink received, wallet disabled`() {
        // WHEN
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(false)
        whenever(walletRepository.getWalletDeepLinkPathState()).thenReturn(true)
        sut.checkWalletEnabled()

        // THEN
        assertFalse(sut.walletEnabled.value)
        assertFalse(sut.isDeeplinkRoute.value)
    }
}
