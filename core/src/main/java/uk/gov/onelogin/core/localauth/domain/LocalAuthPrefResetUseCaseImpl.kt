package uk.gov.onelogin.core.localauth.domain

import javax.inject.Inject
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.preference.LocalAuthPreference

class LocalAuthPrefResetUseCaseImpl @Inject constructor(
    private val localAuthPreferenceRepo: LocalAuthPreferenceRepo,
    private val localAuthManager: LocalAuthManager
) : LocalAuthPrefResetUseCase {
    override suspend fun reset() {
        val pref = localAuthManager.localAuthPreference
        if (pref == null || pref == LocalAuthPreference.Disabled) {
            localAuthPreferenceRepo.clean()
        }
    }
}
