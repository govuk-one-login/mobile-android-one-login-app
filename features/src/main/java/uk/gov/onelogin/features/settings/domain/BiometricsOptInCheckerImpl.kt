package uk.gov.onelogin.features.settings.domain

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsManager
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsStatus

class BiometricsOptInCheckerImpl @Inject constructor(
    private val deviceBiometricsManager: DeviceBiometricsManager
) : BiometricsOptInChecker {

    override suspend fun getBiometricsOptInState(): Flow<Boolean> = flow {
        val biometricsAvailable =
            deviceBiometricsManager.getCredentialStatus() == DeviceBiometricsStatus.SUCCESS
        emit(biometricsAvailable)
    }
}
