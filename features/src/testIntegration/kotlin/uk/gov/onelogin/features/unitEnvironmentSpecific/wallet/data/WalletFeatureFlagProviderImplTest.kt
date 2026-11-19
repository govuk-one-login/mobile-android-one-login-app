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

        assertEquals(setOf(WalletFeatureFlag.sharing(true)), featureFlags)
    }

    @Test
    fun `provideFeatureFlags returns sharing flag set to true`() {
        val provider = WalletFeatureFlagProviderImpl()

        val featureFlags = provider.provideFeatureFlags()

        assertContains(featureFlags, WalletFeatureFlag.sharing(true))
    }
}
