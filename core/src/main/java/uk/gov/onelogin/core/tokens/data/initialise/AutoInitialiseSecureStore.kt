package uk.gov.onelogin.core.tokens.data.initialise

import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys

fun interface AutoInitialiseSecureStore {
    /**
     * Initialise the token secure store using id [AuthTokenStoreKeys.TOKEN_SECURE_STORE_ID].
     * The initialisation of the secure store only happens if BiometricPreference is set
     * to either [LocalAuthPreference.Enabled] (biometrics and/ or passcode).
     *
     * Also attempts to save any tokens in the [uk.gov.onelogin.core.tokens.data.TokenRepository]
     */
    suspend fun initialise(refreshToken: String?)
}
