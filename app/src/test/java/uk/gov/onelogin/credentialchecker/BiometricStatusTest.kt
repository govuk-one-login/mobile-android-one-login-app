package uk.gov.onelogin.credentialchecker

import androidx.biometric.BiometricManager
import java.util.stream.Stream
import kotlin.test.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class BiometricStatusTest {

    @ParameterizedTest
    @MethodSource("biometricStatusArgs")
    fun `map BiometricManager status code to BiometricStatus`(
        statusCode: Int,
        expected: BiometricStatus
    ) {
        // Given an Android BiometricManager status code
        // When calling `forAndroidInt`
        val actual = BiometricStatus.forAndroidInt(statusCode)
        // Then the actual BiometricStatus matches the expected
        assertEquals(expected, actual)
    }

    companion object {
        @JvmStatic
        fun biometricStatusArgs(): Stream<Arguments> = Stream.of(
            Arguments.of(
                BiometricManager.BIOMETRIC_SUCCESS,
                BiometricStatus.SUCCESS
            ),
            Arguments.of(
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                BiometricStatus.NO_HARDWARE
            ),
            Arguments.of(
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
                BiometricStatus.HARDWARE_UNAVAILABLE
            ),
            Arguments.of(
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED,
                BiometricStatus.NOT_ENROLLED
            ),
            Arguments.of(
                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,
                BiometricStatus.UNKNOWN
            )
        )
    }
}
