package uk.gov.onelogin.features.settings.domain

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsManager
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsStatus

class BiometricsOptInCheckerTest {
    private lateinit var deviceBiometricsManager: DeviceBiometricsManager
    private lateinit var biometricsOptInChecker: BiometricsOptInChecker

    @Before
    fun setup() {
        deviceBiometricsManager = mock()
        biometricsOptInChecker = BiometricsOptInCheckerImpl(deviceBiometricsManager)
    }

    @Test
    fun `verify biometrics are available`() = runTest {
        whenever(deviceBiometricsManager.getCredentialStatus())
            .thenReturn(DeviceBiometricsStatus.SUCCESS)

        var result = false

        biometricsOptInChecker.getBiometricsOptInState().collectLatest { result = it }

        assertTrue(result)
    }

    @Test
    fun `verify biometrics are not available - NOT_ENROLLED`() = runTest {
        whenever(deviceBiometricsManager.getCredentialStatus())
            .thenReturn(DeviceBiometricsStatus.NOT_ENROLLED)

        var result = false

        biometricsOptInChecker.getBiometricsOptInState().collectLatest { result = it }

        assertFalse(result)
    }

    @Test
    fun `verify biometrics are not available - UNKNOWN`() = runTest {
        whenever(deviceBiometricsManager.getCredentialStatus())
            .thenReturn(DeviceBiometricsStatus.UNKNOWN)

        var result = false

        biometricsOptInChecker.getBiometricsOptInState().collectLatest { result = it }

        assertFalse(result)
    }

    @Test
    fun `verify biometrics are not available - NO_HARDWARE`() = runTest {
        whenever(deviceBiometricsManager.getCredentialStatus())
            .thenReturn(DeviceBiometricsStatus.NO_HARDWARE)

        var result = false

        biometricsOptInChecker.getBiometricsOptInState().collectLatest { result = it }

        assertFalse(result)
    }

    @Test
    fun `verify biometrics are not available - HARDWARE_UNAVAILABLE`() = runTest {
        whenever(deviceBiometricsManager.getCredentialStatus())
            .thenReturn(DeviceBiometricsStatus.HARDWARE_UNAVAILABLE)

        var result = false

        biometricsOptInChecker.getBiometricsOptInState().collectLatest { result = it }

        assertFalse(result)
    }
}
