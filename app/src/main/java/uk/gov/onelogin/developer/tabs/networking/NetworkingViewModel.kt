package uk.gov.onelogin.developer.tabs.networking

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.onelogin.appcheck.AppCheck

@HiltViewModel
class NetworkingViewModel @Inject constructor(
    val appCheck: AppCheck
) : ViewModel() {
    val appCheckToken: MutableState<String> = mutableStateOf("")
    fun getToken() {
        appCheck.getAppCheckToken(
            onSuccess = { token ->
                appCheckToken.value = token
            },
            onFailure = { error ->
                appCheckToken.value = "error: " + error.localizedMessage
            }
        )
    }
}
