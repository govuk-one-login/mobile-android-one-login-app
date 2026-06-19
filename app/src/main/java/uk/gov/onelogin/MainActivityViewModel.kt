package uk.gov.onelogin

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.android.network.auth.AuthenticationProvider
import uk.gov.android.network.client.GenericHttpClient
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel
    @Inject
    constructor(
        private val genericHttpClient: GenericHttpClient,
        private val authenticationProvider: AuthenticationProvider,
    ) : ViewModel() {
        fun setHttpClientAuthProvider() {
            genericHttpClient.setAuthenticationProvider(authenticationProvider)
        }
    }
