package uk.gov.onelogin.login.ui.biooptin

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.onelogin.extensions.CoroutinesTestExtension
import uk.gov.onelogin.extensions.InstantExecutorExtension
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.mainnav.MainNavRoutes
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class BioOptInViewModelTest {
    private val mockBioPrefHandler: BiometricPreferenceHandler = mock()
    private val mockAutoInitialiseSecureStore: AutoInitialiseSecureStore = mock()
    private val mockNavigator: Navigator = mock()

    private val viewModel =
        BioOptInViewModel(
            mockBioPrefHandler,
            mockAutoInitialiseSecureStore,
            mockNavigator
        )

    @Test
    fun `use biometrics`() = runTest {
        viewModel.useBiometrics()

        verify(mockAutoInitialiseSecureStore).initialise()
        verify(mockBioPrefHandler).setBioPref(BiometricPreference.BIOMETRICS)
        verify(mockNavigator).navigate(MainNavRoutes.Start, true)
    }

    @Test
    fun `not use biometrics`() = runTest {
        viewModel.doNotUseBiometrics()

        verify(mockAutoInitialiseSecureStore).initialise()
        verify(mockBioPrefHandler).setBioPref(BiometricPreference.PASSCODE)
        verify(mockNavigator).navigate(MainNavRoutes.Start, true)
    }
}
