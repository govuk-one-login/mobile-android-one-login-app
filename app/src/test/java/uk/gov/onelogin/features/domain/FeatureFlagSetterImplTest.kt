package uk.gov.onelogin.features.domain

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import uk.gov.android.features.InMemoryFeatureFlags
import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import uk.gov.onelogin.features.AppCheckFeatureFlag

class FeatureFlagSetterImplTest {
    private val featureFlags: InMemoryFeatureFlags = mock()

    private lateinit var setFeatureFlagsImpl: FeatureFlagSetterImpl

    @BeforeEach
    fun setUp() {
        setFeatureFlagsImpl = FeatureFlagSetterImpl(featureFlags)
    }

    @Test
    fun `fromAppInfo() - Success - AppCheckEnabled true - should add AppCheckFeatureFlag`() {
        // Given
        val data = AppInfoData.AppInfo(
            minimumVersion = "0.0.0",
            releaseFlags = AppInfoData.ReleaseFlags(
                true,
                true,
                true
            ),
            available = true,
            featureFlags = AppInfoData.FeatureFlags(true)
        )

        // When
        setFeatureFlagsImpl.setFromAppInfo(data)

        // Then
        verify(featureFlags).plusAssign(setOf(AppCheckFeatureFlag.ENABLED))
        verify(featureFlags, times(0)).minusAssign(setOf(AppCheckFeatureFlag.ENABLED))
    }

    @Test
    fun `fromAppInfo() - Success - AppCheckEnabled false - should remove AppCheckFeatureFlag`() {
        // Given
        val data = AppInfoData.AppInfo(
            minimumVersion = "0.0.0",
            releaseFlags = AppInfoData.ReleaseFlags(
                true,
                true,
                true
            ),
            available = true,
            featureFlags = AppInfoData.FeatureFlags(false)
        )

        // When
        setFeatureFlagsImpl.setFromAppInfo(data)

        // Then
        verify(featureFlags, times(0)).plusAssign(setOf(AppCheckFeatureFlag.ENABLED))
        verify(featureFlags).minusAssign(setOf(AppCheckFeatureFlag.ENABLED))
    }
}
