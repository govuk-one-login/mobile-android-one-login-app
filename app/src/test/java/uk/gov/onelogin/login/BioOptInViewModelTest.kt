package uk.gov.onelogin.login

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.login.ui.biooptin.BioOptInViewModel
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore

class BioOptInViewModelTest {
    private val mockBioPrefHandler: BiometricPreferenceHandler = mock()
    private val mockAutoInitialiseSecureStore: AutoInitialiseSecureStore = mock()

    private val viewModel =
        BioOptInViewModel(mockBioPrefHandler, mockAutoInitialiseSecureStore)

    @Test
    fun `check setting bio pref`() {
        viewModel.setBioPreference(BiometricPreference.BIOMETRICS)

        verify(mockBioPrefHandler).setBioPref(BiometricPreference.BIOMETRICS)
    }

    @Test
    fun `check secure store initialised`() {
        viewModel.setBioPreference(BiometricPreference.BIOMETRICS)

        verify(mockAutoInitialiseSecureStore).invoke()
    }
}
