package uk.gov.onelogin.features.signout.ui

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.extensions.InstantExecutorExtension
import uk.gov.onelogin.features.signout.domain.SignOutError
import uk.gov.onelogin.features.signout.domain.SignOutUseCase
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCaseImpl

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class SignOutViewModelTest {
    private lateinit var viewModel: SignOutViewModel

    private val mockNavigator: Navigator = mock()
    private val mockSignOutUseCase: SignOutUseCase = mock()
    private val logger = SystemLogger()

    @BeforeEach
    fun setup() {
        viewModel =
            SignOutViewModel(
                mockNavigator,
                mockSignOutUseCase,
                logger
            )
    }

    @Test
    fun `sign out use case does not throw`() =
        runTest {
            viewModel.signOut()

            verify(mockSignOutUseCase).invoke()
            verify(mockNavigator).navigate(SignOutRoutes.Success)
            assertThat("logger has no logs", logger.size == 0)
        }

    @Test
    fun `sign out use case does throw`() =
        runTest {
            whenever(mockSignOutUseCase.invoke()).thenThrow(
                SignOutError(Exception("test"))
            )

            viewModel.signOut()

            verify(mockSignOutUseCase).invoke()
            verify(mockNavigator).navigate(ErrorRoutes.SignOutError, false)
            assertThat("logger has log", logger.contains("java.lang.Exception: test"))
        }

    @Test
    fun `sign out use case does throw wallet error`() =
        runTest {
            whenever(mockSignOutUseCase.invoke()).thenThrow(
                SignOutError(DeleteWalletDataUseCaseImpl.DeleteWalletDataError())
            )

            viewModel.signOut()

            verify(mockSignOutUseCase).invoke()
            verify(mockNavigator).navigate(SignOutRoutes.SignOutWalletError, false)
            assertThat(
                "logger has log",
                logger.contains(DeleteWalletDataUseCaseImpl.DeleteWalletDataError().toString())
            )
        }

    @Test
    fun `goBack() correctly navigates back`() {
        viewModel.goBack()

        verify(mockNavigator).goBack()
    }
}
