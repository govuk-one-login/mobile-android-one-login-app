package uk.gov.onelogin.developer.tabs.appintegrity

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.android.authentication.integrity.ClientAttestationManager
import uk.gov.android.authentication.integrity.appcheck.usecase.AppChecker
import uk.gov.onelogin.appcheck.AppIntegrity

@HiltViewModel
class AppIntegrityTabViewModel @Inject constructor(
    private val firebaseAppCheck: AppChecker,
    private val appCheck: ClientAttestationManager,
    private val appIntegrity: AppIntegrity
) : ViewModel() {
    val tokenResponse: MutableState<String> = mutableStateOf("")
    val networkResponse: MutableState<String> = mutableStateOf("")
    val clientAttestationResult: MutableState<String> = mutableStateOf("")
    val proofOfPossessionResult: MutableState<String> = mutableStateOf("")

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

    fun getClientAttestation() {
        viewModelScope.launch {
            clientAttestationResult.value = "Loading..."
            clientAttestationResult.value = appIntegrity.getClientAttestation().toString()
        }
    }

    fun generatePoP() {
        proofOfPossessionResult.value = "Loading..."
        proofOfPossessionResult.value = appIntegrity.getProofOfPossession().toString()
    }
}
