package uk.gov.onelogin.developer.tabs.networking

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.android.authentication.integrity.ClientAttestationManager
import uk.gov.android.authentication.integrity.appcheck.AppChecker
import uk.gov.onelogin.appcheck.AppIntegrity

@HiltViewModel
class NetworkingViewModel @Inject constructor(
    private val firebaseAppCheck: AppChecker,
    private val appCheck: ClientAttestationManager,
    private val appIntegrity: AppIntegrity
) : ViewModel() {
    val tokenResponse: MutableState<String> = mutableStateOf("")
    val networkResponse: MutableState<String> = mutableStateOf("")
    val appIntegrityResult: MutableState<String> = mutableStateOf("")

    fun getToken() {
        viewModelScope.launch {
            tokenResponse.value = "Loading..."
            tokenResponse.value = firebaseAppCheck.getAppCheckToken().toString()
        }
    }

    fun makeNetworkCall() {
        viewModelScope.launch {
            networkResponse.value = "Loading..."
            networkResponse.value = appCheck.getAttestation().toString()
        }
    }

    fun makeAppIntegrityCheck() {
        viewModelScope.launch {
            appIntegrityResult.value = "Loading..."
            appIntegrityResult.value = appIntegrity.startCheck().toString()
        }
    }
}
