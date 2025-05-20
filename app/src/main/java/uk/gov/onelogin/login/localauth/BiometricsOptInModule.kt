package uk.gov.onelogin.login.localauth

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsManager
import uk.gov.onelogin.features.settings.domain.BiometricsOptInChecker
import uk.gov.onelogin.features.settings.domain.BiometricsOptInCheckerImpl

@Module
@InstallIn(ViewModelComponent::class)
object BiometricsOptInModule {
    @Provides
    fun provideBiometricsOptInChecker(
        deviceBiometricsManager: DeviceBiometricsManager
    ): BiometricsOptInChecker = BiometricsOptInCheckerImpl(deviceBiometricsManager)
}
