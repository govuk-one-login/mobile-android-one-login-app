package uk.gov.onelogin.core.localauth.domain

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.android.localauth.preference.LocalAuthPreferenceRepository
import uk.gov.onelogin.core.cleaner.domain.Cleaner
import javax.inject.Inject

interface LocalAuthPreferenceRepo :
    LocalAuthPreferenceRepository,
    Cleaner

class LocalAuthPreferenceRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext
        private val context: Context,
    ) : LocalAuthPreferenceRepo {
        private val sharedPrefs =
            context.getSharedPreferences(
                SHARED_PREFS_ID,
                Context.MODE_PRIVATE,
            )

        override fun setLocalAuthPref(pref: LocalAuthPreference) {
            sharedPrefs.edit {
                putString(LOCAL_AUTH_PREF, pref.toString())
            }
        }

        override fun getLocalAuthPref(): LocalAuthPreference? =
            when (sharedPrefs.getString(LOCAL_AUTH_PREF, null)) {
                LocalAuthPreference.Enabled(true).toString() -> LocalAuthPreference.Enabled(true)
                LocalAuthPreference.Enabled(false).toString() -> LocalAuthPreference.Enabled(false)
                LocalAuthPreference.Disabled.toString() -> LocalAuthPreference.Disabled
                else -> null
            }

        override suspend fun clean(): Result<Unit> {
            sharedPrefs.edit {
                clear()
                commit()
            }
            return Result.success(Unit)
        }

        companion object {
            @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
            const val SHARED_PREFS_ID = "local_auth_shared_prefs"

            @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
            const val LOCAL_AUTH_PREF = "local_auth_pref"
        }
    }
