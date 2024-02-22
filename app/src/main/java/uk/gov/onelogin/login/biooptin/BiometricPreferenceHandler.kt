package uk.gov.onelogin.login.biooptin

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface BiometricPreferenceHandler {
    fun setBioPref(pref: BiometricPreference)

    fun getBioPref(): BiometricPreference?
}

class BiometricPreferenceHandlerImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
) : BiometricPreferenceHandler {
    private val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_ID, Context.MODE_PRIVATE)

    override fun setBioPref(pref: BiometricPreference) {
        with(sharedPrefs.edit()) {
            putString(BIO_PREF, pref.name)
            apply()
        }
    }

    override fun getBioPref(): BiometricPreference? {
        return when (sharedPrefs.getString(BIO_PREF, null)) {
            BiometricPreference.BIOMETRICS.name -> BiometricPreference.BIOMETRICS
            BiometricPreference.PASSCODE.name -> BiometricPreference.PASSCODE
            BiometricPreference.NONE.name -> BiometricPreference.NONE
            else -> null
        }
    }

    companion object {
        private const val SHARED_PREFS_ID = "bio_shared_prefs"
        private const val BIO_PREF = "bio_pref"
    }
}
