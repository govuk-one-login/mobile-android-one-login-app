package uk.gov.onelogin.features.unit.signout.ui.info

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.extensions.InstantExecutorExtension
import uk.gov.onelogin.features.signout.ui.info.SignedOutInfoViewModel
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class SignedOutInfoViewModelTest {
    private val tokenRepository: TokenRepository = mock()
    private val navigator: Navigator = mock()

    private val viewModel by lazy {
        SignedOutInfoViewModel(
            navigator,
            tokenRepository,
        )
    }

    @Test
    fun `reset tokens calls use case`() {
        viewModel.resetTokens()

        verify(tokenRepository).clearTokenResponse()
    }

    @Test
    fun `navigator has back stack, reauth is true`() {
        whenever(navigator.hasBackStack()).thenReturn(true)
        assertTrue(viewModel.shouldReAuth())
    }

    @Test
    fun `navigator has no back stack, reauth is false`() {
        whenever(navigator.hasBackStack()).thenReturn(false)
        assertFalse(viewModel.shouldReAuth())
    }
}
