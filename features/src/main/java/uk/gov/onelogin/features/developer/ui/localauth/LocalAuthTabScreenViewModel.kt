package uk.gov.onelogin.features.developer.ui.localauth

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.localauth.LocalAuthManagerCallbackHandler
import uk.gov.android.localauth.LocalAuthManagerImpl
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsStatus
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.core.localauth.domain.LocalAuthPreferenceRepo
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag

@HiltViewModel
class LocalAuthTabScreenViewModel @Inject constructor(
    private val localAuthPreferenceRepo: LocalAuthPreferenceRepo,
    private val analyticsLogger: AnalyticsLogger,
    private val featureFlags: FeatureFlags
) : ViewModel() {

    fun triggerLocalAuthMock(activity: FragmentActivity, isDeviceSecure: Boolean) {
        val localAuthManager = configureLocalAuthManager(isDeviceSecure)
        viewModelScope.launch {
            val realPref = localAuthManager.localAuthPreference
            localAuthPreferenceRepo.setLocalAuthPref(LocalAuthPreference.Disabled)
            localAuthManager.enforceAndSet(
                featureFlags[WalletFeatureFlag.ENABLED],
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
