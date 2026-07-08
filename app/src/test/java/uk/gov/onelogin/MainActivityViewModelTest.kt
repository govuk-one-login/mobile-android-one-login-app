package uk.gov.onelogin

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.network.attestation.ClientAttestationProvider
import uk.gov.android.network.attestation.TestClientAttestationProvider
import uk.gov.android.network.auth.AuthenticationProvider
import uk.gov.android.network.auth.TestAuthenticationProvider
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.network.dpop.DPoPProvider
import uk.gov.android.network.dpop.TestDPoPProvider

class MainActivityViewModelTest {
    private val genericHttpClient: GenericHttpClient = mock()
    private val authenticationProvider: AuthenticationProvider = TestAuthenticationProvider()
    private var dPoPProvider: DPoPProvider = TestDPoPProvider()
    private var clientAttestationProvider: ClientAttestationProvider = TestClientAttestationProvider()
    private val vm =
        MainActivityViewModel(
            genericHttpClient,
            authenticationProvider,
            dPoPProvider,
            clientAttestationProvider,
        )

    @Test
    fun `setHttpClientAuthProvider sets the authentication provider on the http client`() {
        vm.setHttpClientAuthProvider()

        verify(genericHttpClient).setAuthenticationProvider(authenticationProvider)
    }
}
