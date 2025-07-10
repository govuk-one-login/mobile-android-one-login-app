package uk.gov.onelogin.features.wallet.ui

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
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
    fun `wallet visible`() {
        // Test initial state
        assertFalse(sut.walletEnabled.value)

        // WHEN
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(true)
        whenever(walletRepository.getCredential()).thenReturn("")
        sut.checkWalletEnabled()

        // THEN
        assertTrue(sut.walletEnabled.value)
    }

    @Test
    fun `wallet not visible`() {
        // Test initial state
        assertFalse(sut.walletEnabled.value)

        // WHEN
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(false)
        whenever(walletRepository.getCredential()).thenReturn("")
        sut.checkWalletEnabled()

        // THEN
        assertFalse(sut.walletEnabled.value)
    }

    @Test
    fun `wallet has deeplink`() {
        val credential = "credential"

        // WHEN
        whenever(walletRepository.getCredential()).thenReturn(credential)

        // THEN
        assertEquals(credential, sut.getCredential())
    }

    @Test
    fun `wallet has no deeplink`() {
        val credential = ""

        // WHEN
        whenever(walletRepository.getCredential()).thenReturn(credential)

        // THEN
        assertEquals(credential, sut.getCredential())
    }

    @Test
    fun `add credential`() {
        // GIVEN
        val expectedCredential = "credential"

        // WHEN
        whenever(walletRepository.getCredential()).thenReturn(expectedCredential)
        val actualCredential = sut.getCredential()

        // THEN
        assertEquals(expectedCredential, actualCredential)
    }

    @Test
    fun `no wallet deeplink received, wallet enabled`() {
        // GIVEN
        val deeplinkPath = "path"

        // WHEN
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(true)
        whenever(walletRepository.getDeepLinkPath()).thenReturn(deeplinkPath)
        sut.checkWalletEnabled()

        // THEN
        assertFalse(sut.walletDeepLinkReceived.value)
    }

    @Test
    fun `wallet deeplink received, wallet enabled`() {
        // GIVEN
        val deeplinkPath = DEEPLINK_PATH

        // WHEN
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(true)
        whenever(walletRepository.getDeepLinkPath()).thenReturn(deeplinkPath)
        sut.checkWalletEnabled()

        // THEN
        assertTrue(sut.walletDeepLinkReceived.value)
    }

    @Test
    fun `no wallet deeplink received, wallet disabled`() {
        // GIVEN
        val deeplinkPath = "path"

        // WHEN
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(false)
        whenever(walletRepository.getDeepLinkPath()).thenReturn(deeplinkPath)
        sut.checkWalletEnabled()

        // THEN
        assertFalse(sut.walletDeepLinkReceived.value)
    }

    @Test
    fun `wallet deeplink received, wallet disabled`() {
        // GIVEN
        val deeplinkPath = DEEPLINK_PATH

        // WHEN
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(false)
        whenever(walletRepository.getDeepLinkPath()).thenReturn(deeplinkPath)
        sut.checkWalletEnabled()

        // THEN
        assertFalse(sut.walletDeepLinkReceived.value)
    }
}
