package uk.gov.onelogin.developer.tabs.networking

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.onelogin.appcheck.AppCheck
import uk.gov.onelogin.appcheck.usecase.AssertionApiCall

@HiltViewModel
class NetworkingViewModel @Inject constructor(
    private val appCheck: AppCheck,
    private val assertionApiCall: AssertionApiCall
) : ViewModel() {
    val tokenResponse: MutableState<String> = mutableStateOf("")
    val networkResponse: MutableState<String> = mutableStateOf("")

    fun getToken() {
        tokenResponse.value = "Loading..."
        appCheck.getAppCheckToken(
            onSuccess = { token ->
                this.tokenResponse.value = token
            },
            onFailure = { error ->
                tokenResponse.value = "error: " + error.localizedMessage
            }
        )
    }

    fun makeNetworkCall() {
        viewModelScope.launch {
            networkResponse.value = "Loading..."
            networkResponse.value = assertionApiCall(tokenResponse.value)
        }
    }
}
