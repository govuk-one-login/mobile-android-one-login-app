package uk.gov.onelogin.features.unitEnvironmentSpecific.featureflags.data

import kotlin.test.assertContains
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import uk.gov.android.featureflags.FeatureFlag
import uk.gov.onelogin.features.featureflags.data.AppIntegrityFeatureFlag
import uk.gov.onelogin.features.featureflags.data.CriOrchestratorFeatureFlag
import uk.gov.onelogin.features.featureflags.data.InitialFeatureFlagProviderImpl
import uk.gov.onelogin.features.featureflags.data.WalletSharingFeatureFlag

class InitialFeatureFlagProviderImplTest {
    @Test
    fun `provideFeatureFlags returns correct feature flags`() {
        val provider = InitialFeatureFlagProviderImpl()

        val featureFlags = provider.provideFeatureFlags()

        assertEquals(
            setOf<FeatureFlag>(
                CriOrchestratorFeatureFlag.ENABLED,
                WalletSharingFeatureFlag.ENABLED,
            ),
            featureFlags,
        )
    }

    @Test
    fun `provideFeatureFlags enables cri orchestrator`() {
        val provider = InitialFeatureFlagProviderImpl()

        val featureFlags = provider.provideFeatureFlags()

        assertContains(featureFlags, CriOrchestratorFeatureFlag.ENABLED)
    }

    @Test
    fun `provideFeatureFlags enables wallet sharing`() {
        val provider = InitialFeatureFlagProviderImpl()

        val featureFlags = provider.provideFeatureFlags()

        assertContains(featureFlags, WalletSharingFeatureFlag.ENABLED)
    }

    @Test
    fun `provideFeatureFlags doesn't include app integrity flag`() {
        val provider = InitialFeatureFlagProviderImpl()

        val featureFlags = provider.provideFeatureFlags()

        assertNull(featureFlags.find { it == AppIntegrityFeatureFlag.ENABLED })
    }
}
