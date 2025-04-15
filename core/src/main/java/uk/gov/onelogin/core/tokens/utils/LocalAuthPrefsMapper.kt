package uk.gov.onelogin.core.tokens.utils

import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.android.securestore.AccessControlLevel

object LocalAuthPrefsMapper {
    /**
     * LocalAuthPrefsMapper method to map [LocalAuthPreference] options onto [AccessControlLevel] options. Allows user choice to be used by the secure store
     *
     * @param localAuthPref [LocalAuthPreference] set by user
     * @return [AccessControlLevel] that the given [LocalAuthPreference] maps to for use in SecureStore instance
     */
    fun mapAccessControlLevel(localAuthPref: LocalAuthPreference): AccessControlLevel =
        when (localAuthPref) {
            LocalAuthPreference.Disabled -> AccessControlLevel.OPEN
            is LocalAuthPreference.Enabled -> {
                if (localAuthPref.biometricsEnabled) {
                    AccessControlLevel.PASSCODE_AND_CURRENT_BIOMETRICS
                } else {
                    AccessControlLevel.PASSCODE
                }
            }
        }
}
