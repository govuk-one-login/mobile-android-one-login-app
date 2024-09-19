package uk.gov.onelogin.login

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.login.ui.biooptin.BioOptInViewModel
import uk.gov.onelogin.mainnav.MainNavRoutes
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore

class BioOptInViewModelTest {
    private val mockBioPrefHandler: BiometricPreferenceHandler = mock()
    private val mockAutoInitialiseSecureStore: AutoInitialiseSecureStore = mock()
    private val mockNavigator: Navigator = mock()

    private val viewModel =
        BioOptInViewModel(mockBioPrefHandler, mockAutoInitialiseSecureStore, mockNavigator)

    @Test
    fun `use biometrics`() {
        viewModel.useBiometrics()

        verify(mockAutoInitialiseSecureStore).invoke()
        verify(mockBioPrefHandler).setBioPref(BiometricPreference.BIOMETRICS)
        verify(mockNavigator).navigate(MainNavRoutes.Start, true)
    }

    @Test
    fun `not use biometrics`() {
        viewModel.doNotUseBiometrics()

        verify(mockAutoInitialiseSecureStore).invoke()
        verify(mockBioPrefHandler).setBioPref(BiometricPreference.PASSCODE)
        verify(mockNavigator).navigate(MainNavRoutes.Start, true)
    }
}
