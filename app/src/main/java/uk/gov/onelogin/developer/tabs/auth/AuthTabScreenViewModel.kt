package uk.gov.onelogin.developer.tabs.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.onelogin.network.usecase.HelloWorldApiCall
import uk.gov.onelogin.repositiories.TokenRepository

@HiltViewModel
class AuthTabScreenViewModel @Inject constructor(
    private val helloWorldApiCall: HelloWorldApiCall,
    private val tokenRepository: TokenRepository
) : ViewModel() {
    private val _happyHelloWorldResponse = mutableStateOf("")
    val happyHelloWorldResponse: State<String>
        get() = _happyHelloWorldResponse

    private val _happyCallLoading = mutableStateOf(false)
    val happyCallLoading: State<Boolean>
        get() = _happyCallLoading

    private val _sadHelloWorldResponse = mutableStateOf("")
    val sadHelloWorldResponse: State<String>
        get() = _sadHelloWorldResponse

    private val _sadCallLoading = mutableStateOf(false)
    val sadCallLoading: State<Boolean>
        get() = _sadCallLoading

    fun makeHappyHelloWorldCall() {
        _happyCallLoading.value = true
        viewModelScope.launch {
            _happyHelloWorldResponse.value = helloWorldApiCall()
            _happyCallLoading.value = false
        }
    }

    fun makeSadHelloWorldCall() {
        _sadCallLoading.value = true
        viewModelScope.launch {
            val currentToken = tokenRepository.getTokenResponse()
            tokenRepository.clearTokenResponse()
            _sadHelloWorldResponse.value = helloWorldApiCall()
            _sadCallLoading.value = false
            if (currentToken != null) {
                tokenRepository.setTokenResponse(currentToken)
            }
        }
    }
}
