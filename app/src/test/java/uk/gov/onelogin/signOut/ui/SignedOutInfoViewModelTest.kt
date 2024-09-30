package uk.gov.onelogin.signOut.ui

import kotlin.test.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.onelogin.extensions.CoroutinesTestExtension
import uk.gov.onelogin.extensions.InstantExecutorExtension
import uk.gov.onelogin.login.usecase.SaveTokens
import uk.gov.onelogin.repositiories.TokenRepository

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class SignedOutInfoViewModelTest {
    private val mockTokenRepository: TokenRepository = mock()
    private val mockSaveTokens: SaveTokens = mock()

    private val viewModel by lazy {
        SignedOutInfoViewModel(
            mockTokenRepository,
            mockSaveTokens
        )
    }

    @Test
    fun `reset tokens calls use case`() {
        viewModel.resetTokens()

        verify(mockTokenRepository).clearTokenResponse()
    }

    @Test
    fun `save tokens calls use case`() = runTest {
        viewModel.saveTokens()

        verify(mockSaveTokens).invoke()
    }
}
