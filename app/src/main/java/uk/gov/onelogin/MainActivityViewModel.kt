package uk.gov.onelogin

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.android.network.attestation.ClientAttestationProvider
import uk.gov.android.network.auth.AuthenticationProvider
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.network.dpop.DPoPProvider
import uk.gov.android.network.service.v2.DefaultNetworkService
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel
    @Inject
    constructor(
        private val genericHttpClient: GenericHttpClient,
        private val defaultNetworkService: DefaultNetworkService,
        private val authenticationProvider: AuthenticationProvider,
        private val dPoPProvider: DPoPProvider,
        private val clientAttestationProvider: ClientAttestationProvider,
    ) : ViewModel() {
        fun initialiseNetworkService() {
            genericHttpClient.setAuthenticationProvider(authenticationProvider)
            defaultNetworkService.apply {
                setAuthenticationProvider(authenticationProvider)
                setDPoPProvider(dPoPProvider)
                setClientAttestationProvider(clientAttestationProvider)
            }
        }
    }
