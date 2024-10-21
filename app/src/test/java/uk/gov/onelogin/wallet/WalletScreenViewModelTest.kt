package uk.gov.onelogin.wallet

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.features.FeatureFlags
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.WalletFeatureFlag

class WalletScreenViewModelTest {
    private val walletSdk: WalletSdk = mock()
    private val featureFlags: FeatureFlags = mock()
    private val sut = WalletScreenViewModel(walletSdk, featureFlags)

    @Test
    fun `wallet visible`() {
        // Test initial state
        assertFalse(sut.enabled.value)

        // WHEN
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(true)
        sut.isWalletEnabled()

        // THEN
        assertTrue(sut.enabled.value)
    }

    @Test
    fun `wallet not visible`() {
        // Test initial state
        assertFalse(sut.enabled.value)

        // WHEN
        whenever(featureFlags[eq(WalletFeatureFlag.ENABLED)]).thenReturn(false)
        sut.isWalletEnabled()

        // THEN
        assertFalse(sut.enabled.value)
    }
}
