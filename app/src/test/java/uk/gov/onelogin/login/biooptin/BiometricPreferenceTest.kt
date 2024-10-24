package uk.gov.onelogin.login.biooptin

import kotlin.test.Test
import kotlin.test.assertEquals

class BiometricPreferenceTest {

    @Test
    fun biometrics() {
        val preference = BiometricPreference.BIOMETRICS
        assertEquals(expected = preference, actual = BiometricPreference.valueOf("BIOMETRICS"))
    }

    @Test
    fun none() {
        val preference = BiometricPreference.NONE
        assertEquals(expected = preference, actual = BiometricPreference.valueOf("NONE"))
    }

    @Test
    fun passcode() {
        val preference = BiometricPreference.PASSCODE
        assertEquals(expected = preference, actual = BiometricPreference.valueOf("PASSCODE"))
    }
}
