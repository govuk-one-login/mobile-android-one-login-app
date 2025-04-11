package uk.gov.onelogin.features.developer.ui.localauth

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import uk.gov.android.localauth.LocalAuthManagerCallbackHandler
import uk.gov.android.localauth.LocalAuthManagerImpl
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsStatus
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.core.localauth.domain.LocalAuthPreferenceRepo

@HiltViewModel
class LocalAuthTabScreenViewModel @Inject constructor(
    private val localAuthPreferenceRepo: LocalAuthPreferenceRepo,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    fun triggerLocalAuthMock(activity: FragmentActivity, isDeviceSecure: Boolean) {
        val localAuthManager = configureLocalAuthManager(isDeviceSecure)
        viewModelScope.launch {
            val realPref = localAuthManager.localAuthPreference
            localAuthPreferenceRepo.setLocalAuthPref(LocalAuthPreference.Disabled)
            localAuthManager.enforceAndSet(
                true,
                activity,
                object : LocalAuthManagerCallbackHandler {
                    override fun onFailure(backButtonPressed: Boolean) {
                        realPref?.let { localAuthPreferenceRepo.setLocalAuthPref(it) }
                    }

                    override fun onSuccess(backButtonPressed: Boolean) {
                        realPref?.let { localAuthPreferenceRepo.setLocalAuthPref(it) }
                    }
                }
            )
        }
    }

    private fun configureLocalAuthManager(displayError: Boolean): LocalAuthManagerImpl {
        val biometricsManager = MockDeviceBiometricManager(
            displayError,
            DeviceBiometricsStatus.SUCCESS
        )

        return LocalAuthManagerImpl(
            localAuthPreferenceRepo,
            biometricsManager,
            analyticsLogger
        )
    }
}
