package uk.gov.onelogin.features.signout.ui

import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.extensions.InstantExecutorExtension
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag
import uk.gov.onelogin.features.signout.domain.SignOutError
import uk.gov.onelogin.features.signout.domain.SignOutUIState
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class SignOutViewModelTest {
    private lateinit var viewModel: SignOutViewModel

    private val mockNavigator: Navigator = mock()
    private val mockSignOutUseCase: SignOutUseCase = mock()
    private val featureFlags: FeatureFlags = mock()
    private val logger = SystemLogger()

    @BeforeEach
    fun setup() {
        viewModel =
            SignOutViewModel(
                mockNavigator,
                mockSignOutUseCase,
                featureFlags,
                logger
            )
    }

    @Test
    fun `sign out use case does not throw`() =
        runTest {
            whenever(featureFlags[WalletFeatureFlag.ENABLED]).then { true }
            viewModel.signOut()

            verify(mockSignOutUseCase).invoke()
            verify(mockNavigator).navigate(SignOutRoutes.Success)
            assertThat("logger has no logs", logger.size == 0)
        }

    @Test
    fun `sign out use case does throw`() =
        runTest {
            whenever(featureFlags[WalletFeatureFlag.ENABLED]).then { true }
            whenever(mockSignOutUseCase.invoke()).thenThrow(
                SignOutError(Exception("test"))
            )

            viewModel.signOut()

            verify(mockSignOutUseCase).invoke()
            verify(mockNavigator).navigate(SignOutRoutes.SignOutError, false)
            assertThat("logger has log", logger.contains("java.lang.Exception: test"))
        }

    @Test
    fun `sign out use case does throw wallet disabled`() =
        runTest {
            whenever(featureFlags[WalletFeatureFlag.ENABLED]).then { false }
            whenever(mockSignOutUseCase.invoke()).thenThrow(
                SignOutError(Exception("test"))
            )

            viewModel.signOut()

            verify(mockSignOutUseCase).invoke()
            verify(mockNavigator).navigate(ErrorRoutes.SignOutWalletDisabled, false)
            assertThat("logger has log", logger.contains("java.lang.Exception: test"))
        }

    @Test
    fun `goBack() correctly navigates back`() {
        whenever(featureFlags[WalletFeatureFlag.ENABLED]).then { true }
        viewModel.goBack()

        verify(mockNavigator).goBack()
    }

    @Test
    fun `uiState Wallet`() {
        whenever(featureFlags[WalletFeatureFlag.ENABLED]).then { true }
        val actual = viewModel.uiState
        assertEquals(expected = SignOutUIState.Wallet, actual = actual)
    }

    @Test
    fun `uiState No Wallet`() {
        whenever(featureFlags[WalletFeatureFlag.ENABLED]).then { false }
        val actual = viewModel.uiState
        assertEquals(expected = SignOutUIState.NoWallet, actual = actual)
    }
}
