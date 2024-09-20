package uk.gov.onelogin.signOut.ui

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.signOut.domain.SignOutError
import uk.gov.onelogin.signOut.domain.SignOutUseCase
import uk.gov.onelogin.ui.error.ErrorRoutes

class SignOutViewModelTest {
    private lateinit var viewModel: SignOutViewModel

    private val mockNavigator: Navigator = mock()
    private val mockSignOutUseCase: SignOutUseCase = mock()

    @BeforeEach
    fun setup() {
        viewModel = SignOutViewModel(
            mockNavigator,
            mockSignOutUseCase
        )
    }

    @Test
    fun `sign out use case does not throw`() {
        viewModel.signOut()

        verify(mockSignOutUseCase).invoke()
        verify(mockNavigator).navigate(LoginRoutes.Root, true)
    }

    @Test
    fun `sign out use case does throw`() {
        whenever(mockSignOutUseCase.invoke()).thenThrow(SignOutError(Exception()))

        viewModel.signOut()

        verify(mockSignOutUseCase).invoke()
        verify(mockNavigator).navigate(ErrorRoutes.SignOut)
    }

    @Test
    fun `goBack() correctly navigates back`() {
        viewModel.goBack()

        verify(mockNavigator).goBack()
    }
}
