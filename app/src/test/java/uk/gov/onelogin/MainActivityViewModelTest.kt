package uk.gov.onelogin

import android.content.Context
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsTokenExpired
import uk.gov.onelogin.core.utils.ActivityProvider
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchange
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

class MainActivityViewModelTest {
    private var activityProvider: ActivityProvider = mock()
    private var context: Context = mock()
    private var genericHttpClient: GenericHttpClient = mock()
    private var tokenRepository: TokenRepository = mock()
    private var isAccessTokenExpired: IsTokenExpired = mock()
    private var navigator: Navigator = mock()
    private var refreshExchange: RefreshExchange = mock()
    private var signOutUseCase: SignOutUseCase = mock()
    private var logger: Logger = mock()
    private var vm: MainActivityViewModel =
        MainActivityViewModel(
            activityProvider,
            context,
            genericHttpClient,
            tokenRepository,
            isAccessTokenExpired,
            navigator,
            refreshExchange,
            signOutUseCase,
            logger
        )

    @Test
    fun `test sts provider setter`() {
        whenever(context.getString(any())).thenReturn("")
        whenever(context.getString(any(), any<String>())).thenReturn("")
        vm.setHttpClientAuthProvider()

        verify(genericHttpClient).setAuthenticationProvider(any())
    }
}
