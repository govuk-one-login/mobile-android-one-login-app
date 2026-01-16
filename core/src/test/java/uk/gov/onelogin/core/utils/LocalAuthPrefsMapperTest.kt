package uk.gov.onelogin.core.utils

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.android.securestore.AccessControlLevel
import uk.gov.onelogin.core.tokens.utils.LocalAuthPrefsMapper.mapAccessControlLevel
import java.util.stream.Stream
import kotlin.test.assertEquals

class LocalAuthPrefsMapperTest {
    @ParameterizedTest
    @MethodSource("args")
    fun checkACLMappings(
        input: LocalAuthPreference,
        output: AccessControlLevel,
    ) {
        val result = mapAccessControlLevel(input)

        assertEquals(output, result)
    }

    companion object {
        @JvmStatic
        fun args(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    LocalAuthPreference.Enabled(true),
                    AccessControlLevel.PASSCODE_AND_BIOMETRICS,
                ),
                Arguments.of(LocalAuthPreference.Enabled(false), AccessControlLevel.PASSCODE),
                Arguments.of(LocalAuthPreference.Disabled, AccessControlLevel.OPEN),
            )
    }
}
