package uk.gov.onelogin

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.network.api.v2.ApiRequest
import uk.gov.android.network.api.v2.ApiResponse
import uk.gov.android.network.api.v2.ApiResponseAssertions.expectFailure
import uk.gov.android.network.api.v2.ApiResponseAssertions.expectSuccess
import uk.gov.android.network.attestation.ClientAttestationProvider
import uk.gov.android.network.attestation.TestClientAttestationProvider
import uk.gov.android.network.auth.AuthenticationProvider
import uk.gov.android.network.auth.TestAuthenticationProvider
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.network.client.v2.GenericHttpResponse
import uk.gov.android.network.client.v2.StubHttpClient
import uk.gov.android.network.dpop.DPoPProvider
import uk.gov.android.network.dpop.TestDPoPProvider
import uk.gov.android.network.service.DefaultNetworkService

class MainActivityViewModelTest {
    private val genericHttpClient: GenericHttpClient = mock()
    private val okResponse = GenericHttpResponse(200, "ok")
    private val stubHttpClient = StubHttpClient().apply {
        response = okResponse
    }
    private val defaultNetworkService = DefaultNetworkService(stubHttpClient)
    private val authenticationProvider: AuthenticationProvider = TestAuthenticationProvider()
    private var dPoPProvider: DPoPProvider = TestDPoPProvider()
    private var clientAttestationProvider: ClientAttestationProvider = TestClientAttestationProvider()
    private val vm =
        MainActivityViewModel(
            genericHttpClient,
            defaultNetworkService,
            authenticationProvider,
            dPoPProvider,
            clientAttestationProvider,
        )

    @Test
    fun `initialiseNetworkService sets the authentication provider on the http client`() {
        vm.initialiseNetworkService()

        verify(genericHttpClient).setAuthenticationProvider(authenticationProvider)
    }

    @Test
    fun `initialiseNetworkService configures the network service`() = runTest {
        // Before initialising the network service, it won't work
        defaultNetworkService.makeRequest().expectFailure()

        vm.initialiseNetworkService()

        val success = defaultNetworkService.makeRequest().expectSuccess()
        assertEquals(
            ApiResponse.Success(okResponse.body, okResponse.status),
            success
        )
    }

    private suspend fun DefaultNetworkService.makeRequest() =
        makeRequest(
            ApiRequest.Get("https://example.gov.uk")
        ) {
            withAttestation = true
            withAuthentication("scope")
            withRefreshDPoP = true
        }
}
