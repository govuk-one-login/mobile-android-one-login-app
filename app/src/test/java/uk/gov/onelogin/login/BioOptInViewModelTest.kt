package uk.gov.onelogin.login

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler

class BioOptInViewModelTest {
    private val mockBioPrefHandler: BiometricPreferenceHandler = mock()

    private val viewModel = BioOptInViewModel(mockBioPrefHandler)

    @Test
    fun `check setting bio pref`() {
        viewModel.setBioPreference(BiometricPreference.BIOMETRICS)

        verify(mockBioPrefHandler).setBioPref(BiometricPreference.BIOMETRICS)
    }
}
