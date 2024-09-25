package uk.gov.onelogin.developer.tabs.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.network.data.AuthApiResponse
import uk.gov.onelogin.network.usecase.HelloWorldApiCall
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.signOut.SignOutRoutes

@HiltViewModel
class AuthTabScreenViewModel @Inject constructor(
    private val helloWorldApiCall: HelloWorldApiCall,
    private val tokenRepository: TokenRepository,
    private val navigator: Navigator
) : ViewModel() {
    private val _happyHelloWorldResponse = mutableStateOf("")
    val happyHelloWorldResponse: State<String>
        get() = _happyHelloWorldResponse

    private val _happyCallLoading = mutableStateOf(false)
    val happyCallLoading: State<Boolean>
        get() = _happyCallLoading

    private val _authFailingHelloWorldResponse = mutableStateOf("")
    val authFailingHelloWorldResponse: State<String>
        get() = _authFailingHelloWorldResponse

    private val _authFailingCallLoading = mutableStateOf(false)
    val authFailingCallLoading: State<Boolean>
        get() = _authFailingCallLoading

    private val _serviceFailingHelloWorldResponse = mutableStateOf("")
    val serviceFailingHelloWorldResponse: State<String>
        get() = _serviceFailingHelloWorldResponse

    private val _serviceFailingCallLoading = mutableStateOf(false)
    val serviceFailingCallLoading: State<Boolean>
        get() = _serviceFailingCallLoading

    fun makeHappyHelloWorldCall() {
        _happyCallLoading.value = true
        viewModelScope.launch {
            handleApiResponse(helloWorldApiCall.happyPath())
            _happyCallLoading.value = false
        }
    }

    fun makeAuthFailingHelloWorldCall() {
        _authFailingCallLoading.value = true
        viewModelScope.launch {
            val currentToken = tokenRepository.getTokenResponse()
            tokenRepository.clearTokenResponse()
            handleApiResponse(helloWorldApiCall.happyPath())
            _authFailingCallLoading.value = false
            if (currentToken != null) {
                tokenRepository.setTokenResponse(currentToken)
            }
        }
    }

    fun makeServiceFailingHelloWorldCall() {
        _serviceFailingCallLoading.value = true
        viewModelScope.launch {
            handleApiResponse(helloWorldApiCall.errorPath())
            _serviceFailingCallLoading.value = false
        }
    }

    private fun handleApiResponse(response: AuthApiResponse) {
        when (response) {
            AuthApiResponse.AuthExpired ->
                navigator.navigate(SignOutRoutes.Info)

            is AuthApiResponse.Failure -> {
                _happyHelloWorldResponse.value = response.e.message ?: "Error"
                _happyCallLoading.value = false
            }

            is AuthApiResponse.Success<*> -> {
                _happyHelloWorldResponse.value = response.response.toString()
                _happyCallLoading.value = false
            }
        }
    }
}
