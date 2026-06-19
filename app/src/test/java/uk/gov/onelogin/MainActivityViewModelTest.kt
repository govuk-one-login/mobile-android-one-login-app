package uk.gov.onelogin

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.network.auth.AuthenticationProvider
import uk.gov.android.network.client.GenericHttpClient

class MainActivityViewModelTest {
    private val genericHttpClient: GenericHttpClient = mock()
    private val authenticationProvider: AuthenticationProvider = mock()
    private val vm = MainActivityViewModel(genericHttpClient, authenticationProvider)

    @Test
    fun `setHttpClientAuthProvider sets the authentication provider on the http client`() {
        vm.setHttpClientAuthProvider()

        verify(genericHttpClient).setAuthenticationProvider(authenticationProvider)
    }
}
