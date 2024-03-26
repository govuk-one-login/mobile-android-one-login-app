package uk.gov.onelogin.tokens

import java.util.stream.Stream
import kotlin.test.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.android.securestore.AccessControlLevel
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.tokens.Mapper.mapAccessControlLevel

class MapperTest {
    @ParameterizedTest
    @MethodSource("args")
    fun checkACLMappings(input: BiometricPreference, output: AccessControlLevel) {
        val result = mapAccessControlLevel(input)

        assertEquals(output, result)
    }

    companion object {
        @JvmStatic
        fun args(): Stream<Arguments> = Stream.of(
            Arguments.of(
                BiometricPreference.BIOMETRICS,
                AccessControlLevel.PASSCODE_AND_CURRENT_BIOMETRICS
            ),
            Arguments.of(BiometricPreference.PASSCODE, AccessControlLevel.PASSCODE),
            Arguments.of(BiometricPreference.NONE, AccessControlLevel.OPEN)
        )
    }
}
