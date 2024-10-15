package uk.gov.onelogin.features.domain

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.android.features.InMemoryFeatureFlags
import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import uk.gov.onelogin.appinfo.source.domain.model.AppInfoLocalState
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource
import uk.gov.onelogin.features.AppCheckFeatureFlag

class SetFeatureFlagsImplTest {
    private val featureFlags: InMemoryFeatureFlags = mock()
    private val appInfoLocalSource: AppInfoLocalSource = mock()

    private lateinit var setFeatureFlagsImpl: SetFeatureFlagsImpl

    @BeforeEach
    fun setUp() {
        setFeatureFlagsImpl = SetFeatureFlagsImpl(featureFlags, appInfoLocalSource)
    }

    @Test
    fun `fromAppInfo() - Success - AppCheckEnabled true - should add AppCheckFeatureFlag`() {
        // Given
        val data = AppInfoData(
            apps = AppInfoData.App(
                AppInfoData.AppInfo(
                    minimumVersion = "0.0.0",
                    releaseFlags = AppInfoData.ReleaseFlags(
                        true,
                        true,
                        true
                    ),
                    available = true,
                    featureFlags = AppInfoData.FeatureFlags(true)
                )
            )
        )

        whenever(appInfoLocalSource.get()).thenReturn(
            AppInfoLocalState.Success(
                data
            )
        )

        // When
        setFeatureFlagsImpl.fromAppInfo()

        // Then
        verify(featureFlags).plusAssign(setOf(AppCheckFeatureFlag.ENABLED))
        verify(featureFlags, times(0)).minusAssign(setOf(AppCheckFeatureFlag.ENABLED))
    }

    @Test
    fun `fromAppInfo() - Success - AppCheckEnabled false - should remove AppCheckFeatureFlag`() {
        // Given
        val data = AppInfoData(
            apps = AppInfoData.App(
                AppInfoData.AppInfo(
                    minimumVersion = "0.0.0",
                    releaseFlags = AppInfoData.ReleaseFlags(
                        true,
                        true,
                        true
                    ),
                    available = true,
                    featureFlags = AppInfoData.FeatureFlags(false)
                )
            )
        )
        whenever(appInfoLocalSource.get()).thenReturn(
            AppInfoLocalState.Success(
                data
            )
        )

        // When
        setFeatureFlagsImpl.fromAppInfo()

        // Then
        verify(featureFlags, times(0)).plusAssign(setOf(AppCheckFeatureFlag.ENABLED))
        verify(featureFlags).minusAssign(setOf(AppCheckFeatureFlag.ENABLED))
    }

    @Test
    fun `fromAppInfo() - Failure - should log error and do nothing`() {
        // Given
        val exception = Exception("Failed to read app info")
        whenever(appInfoLocalSource.get()).thenReturn(
            AppInfoLocalState.Failure(
                reason = "Failed to read app info",
                exp = exception
            )
        )

        // When
        setFeatureFlagsImpl.fromAppInfo()

        // Then
        verifyNoInteractions(featureFlags)
    }
}
