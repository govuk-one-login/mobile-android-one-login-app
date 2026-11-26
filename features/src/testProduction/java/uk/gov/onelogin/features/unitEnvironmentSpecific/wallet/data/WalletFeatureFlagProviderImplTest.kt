package uk.gov.onelogin.features.unitEnvironmentSpecific.wallet.data

import kotlin.test.assertContains
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import uk.gov.android.wallet.sdk.WalletFeatureFlag
import uk.gov.onelogin.features.wallet.data.WalletFeatureFlagProviderImpl

class WalletFeatureFlagProviderImplTest {
    @Test
    fun `provideFeatureFlags returns correct feature flags`() {
        val provider = WalletFeatureFlagProviderImpl()

        val featureFlags = provider.provideFeatureFlags()

        assertEquals(setOf(WalletFeatureFlag.sharing(false)), featureFlags)
    }

    @Test
    fun `provideFeatureFlags returns sharing set to false`() {
        val provider = WalletFeatureFlagProviderImpl()

        val featureFlags = provider.provideFeatureFlags()

        assertContains(featureFlags, WalletFeatureFlag.sharing(false))
    }
}
