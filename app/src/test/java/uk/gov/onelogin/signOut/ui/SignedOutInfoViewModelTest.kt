package uk.gov.onelogin.signOut.ui

import androidx.fragment.app.FragmentActivity
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.onelogin.extensions.CoroutinesTestExtension
import uk.gov.onelogin.extensions.InstantExecutorExtension
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.usecase.SaveTokens
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.signOut.domain.SignOutError
import uk.gov.onelogin.signOut.domain.SignOutUseCase
import uk.gov.onelogin.tokens.usecases.GetPersistentId

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class SignedOutInfoViewModelTest {
    private val mockTokenRepository: TokenRepository = mock()
    private val mockSaveTokens: SaveTokens = mock()
    private val mockNavigator: Navigator = mock()
    private val mockGetPersistentId: GetPersistentId = mock()
    private val mockSignOutUseCase: SignOutUseCase = mock()

    private val viewModel by lazy {
        SignedOutInfoViewModel(
            mockNavigator,
            mockTokenRepository,
            mockSaveTokens,
            mockGetPersistentId,
            mockSignOutUseCase
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

    @Test
    fun `navigator has back stack, reauth is true`() {
        whenever(mockNavigator.hasBackStack()).thenReturn(true)
        assertTrue(viewModel.shouldReAuth())
    }

    @Test
    fun `navigator has no back stack, reauth is false`() {
        whenever(mockNavigator.hasBackStack()).thenReturn(false)
        assertFalse(viewModel.shouldReAuth())
    }

    @Test
    fun `sign out usecase is called when persistent id is null`() = runTest {
        val activity: FragmentActivity = mock()
        whenever(mockGetPersistentId.invoke()).thenReturn(null)

        viewModel.checkPersistentId(activity)

        verify(mockSignOutUseCase).invoke(activity)
        verify(mockNavigator).navigate(LoginRoutes.Welcome, true)
    }

    @Test
    fun `sign out usecase is called when persistent id is empty`() = runTest {
        val activity: FragmentActivity = mock()
        whenever(mockGetPersistentId.invoke()).thenReturn("")

        viewModel.checkPersistentId(activity)

        verify(mockSignOutUseCase).invoke(activity)
        verify(mockNavigator).navigate(LoginRoutes.Welcome, true)
    }

    @Test
    fun `sign out usecase is not called when persistent id good`() = runTest {
        val activity: FragmentActivity = mock()
        whenever(mockGetPersistentId.invoke()).thenReturn("id")

        viewModel.checkPersistentId(activity)

        verifyNoInteractions(mockSignOutUseCase)
        verifyNoInteractions(mockNavigator)
    }

    @Test
    fun `sign out usecase throws`() = runTest {
        val activity: FragmentActivity = mock()
        whenever(mockGetPersistentId.invoke()).thenReturn("")
        whenever(mockSignOutUseCase.invoke(any())).thenThrow(SignOutError(Error()))

        viewModel.checkPersistentId(activity)

        verify(mockSignOutUseCase).invoke(activity)
        verify(mockNavigator).navigate(LoginRoutes.SignInError, true)
    }
}
