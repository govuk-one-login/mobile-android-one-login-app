package uk.gov.onelogin

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.features.optin.data.AnalyticsOptInRepository

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val analyticsOptInRepo: AnalyticsOptInRepository,
    private val localAuthManager: LocalAuthManager,
    private val tokenRepository: TokenRepository,
    private val navigator: Navigator,
    autoInitialiseSecureStore: AutoInitialiseSecureStore
) : ViewModel(), DefaultLifecycleObserver {

    init {
        viewModelScope.launch {
            autoInitialiseSecureStore.initialise()
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        viewModelScope.launch {
            analyticsOptInRepo.synchronise()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        if (isLocalAuthEnabled() &&
            tokenRepository.getTokenResponse() != null
        ) {
            tokenRepository.clearTokenResponse()
            navigator.navigate(LoginRoutes.Start)
        }
    }

    private fun isLocalAuthEnabled(): Boolean {
        val prefs = localAuthManager.localAuthPreference
        return !(prefs == LocalAuthPreference.Disabled || prefs == null)
    }
}
