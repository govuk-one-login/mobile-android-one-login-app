package uk.gov.onelogin.developer.tabs.networking

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.onelogin.integrity.appcheck.AppChecker
import uk.gov.onelogin.integrity.appcheck.usecase.AttestationCaller

@HiltViewModel
class NetworkingViewModel @Inject constructor(
    private val appCheck: AppChecker,
    private val assertionApiCall: AttestationCaller
) : ViewModel() {
    val tokenResponse: MutableState<String> = mutableStateOf("")
    val networkResponse: MutableState<String> = mutableStateOf("")

    fun getToken() {
        viewModelScope.launch {
            tokenResponse.value = "Loading..."
            tokenResponse.value = appCheck.getAppCheckToken().toString()
        }
    }

    fun makeNetworkCall() {
        viewModelScope.launch {
            networkResponse.value = "Loading..."
            networkResponse.value = assertionApiCall.call(
                signedProofOfPossession = "",
                "",
                ""
            ).toString()
        }
    }
}
