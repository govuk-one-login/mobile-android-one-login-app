package uk.gov.onelogin

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val bioPrefHandler: BiometricPreferenceHandler,
    private val tokenRepository: TokenRepository,
    autoInitialiseSecureStore: AutoInitialiseSecureStore
) : ViewModel(), DefaultLifecycleObserver {
    private val _next = MutableLiveData<String>()
    val next: LiveData<String> = _next

    init {
        autoInitialiseSecureStore()
    }

    override fun onPause(owner: LifecycleOwner) {
        if (bioPrefHandler.getBioPref() != BiometricPreference.NONE &&
            tokenRepository.getTokenResponse() != null
        ) {
            tokenRepository.clearTokenResponse()
            _next.value = LoginRoutes.START
        }
    }
}
