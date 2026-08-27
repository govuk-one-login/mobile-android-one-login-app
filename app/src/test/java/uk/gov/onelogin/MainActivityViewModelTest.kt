package uk.gov.onelogin

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.network.auth.AuthenticationProvider
import uk.gov.android.network.auth.TestAuthenticationProvider
import uk.gov.android.network.client.GenericHttpClient

class MainActivityViewModelTest {
    private val genericHttpClient: GenericHttpClient = mock()
    private val authenticationProvider: AuthenticationProvider = TestAuthenticationProvider()
    private val vm =
        MainActivityViewModel(
            genericHttpClient,
            authenticationProvider,
        )

    @Test
    fun `initialiseNetworkService sets the authentication provider on the http client`() {
        vm.initialiseNetworkService()

        verify(genericHttpClient).setAuthenticationProvider(authenticationProvider)
    }
}
